import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CuratorTest {
    private Logger log = LoggerFactory.getLogger(CuratorTest.class);

    //会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;

    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    //ZooKeeper服务地址
    private static final String CONNECT_ADDR = "172.16.214.101:2181";


    @Test
    void test_simple_use() throws Exception {
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR).connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
//命名空间           .namespace("super")
                .build();
        //3 开启连接
        client.start();

        System.out.println(ZooKeeper.States.CONNECTED);
        System.out.println(client.getState());

        //创建永久节点
        client.create().forPath("/curator", "/curator data".getBytes());

        //创建永久有序节点
        client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/curator_sequential", "/curator_sequential data".getBytes());

        //创建临时节点
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath("/curator/ephemeral", "/curator/ephemeral data".getBytes());

        //创建临时有序节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator/ephemeral_path1", "/curator/ephemeral_path1 data".getBytes());

        client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator/ephemeral_path2", "/curator/ephemeral_path2 data".getBytes());

        //测试检查某个节点是否存在
        Stat stat1 = client.checkExists().forPath("/curator");
        Stat stat2 = client.checkExists().forPath("/curator2");

        System.out.println("'/curator'是否存在： " + (stat1 != null ? true : false));
        System.out.println("'/curator2'是否存在： " + (stat2 != null ? true : false));

        //获取某个节点的所有子节点
        System.out.println(client.getChildren().forPath("/"));

        //获取某个节点数据
        System.out.println(new String(client.getData().forPath("/curator")));

        //设置某个节点数据
        client.setData().forPath("/curator", "/curator modified data".getBytes());

        //创建测试节点
        client.create().orSetData().creatingParentContainersIfNeeded()
                .forPath("/curator/del_key1", "/curator/del_key1 data".getBytes());

        client.create().orSetData().creatingParentContainersIfNeeded()
                .forPath("/curator/del_key2", "/curator/del_key2 data".getBytes());

        client.create().forPath("/curator/del_key2/test_key", "test_key data".getBytes());

        //删除该节点
        client.delete().forPath("/curator/del_key1");

        //级联删除子节点
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/curator/del_key2");
    }

    /**
     * 事务管理：碰到异常，事务会回滚
     */
    @Test
    public void testTransaction() throws Exception{
        CuratorFramework client = client();
        //定义几个基本操作
        CuratorOp createOp = client.transactionOp().create()
                .forPath("/curator/one_path","some data".getBytes());

        CuratorOp setDataOp = client.transactionOp().setData()
                .forPath("/curator","other data".getBytes());

        CuratorOp deleteOp = client.transactionOp().delete()
                .forPath("/curator");

        //事务执行结果
        Assertions.assertThrows(Exception.class,()->{
            List<CuratorTransactionResult> results = client.transaction()
                    .forOperations(createOp,setDataOp,deleteOp);
            //遍历输出结果
            for(CuratorTransactionResult result : results){
                System.out.println("执行结果是： " + result.getForPath() + "--" + result.getType());
            }
        });
        client.close();

    }

    @Test
    void test_listener() throws Exception {
        CuratorFramework client = client();
        /**
         * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
         */
        ExecutorService pool = Executors.newFixedThreadPool(2);

        /**
         * 监听数据节点的变化情况
         */
        final NodeCache nodeCache = new NodeCache(client, "/zk-huey/cnode", false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(
                new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        System.out.println("Node data is changed, new data: " +
                                new String(nodeCache.getCurrentData().getData()));
                    }
                },
                pool
        );

        /**
         * 监听子节点的变化情况
         */
        final PathChildrenCache childrenCache = new PathChildrenCache(client, "/zk-huey", true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                System.out.println("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }
                },
                pool
        );


        Stat stat = client.checkExists().forPath("/zk-huey/cnode");
        if (stat == null) {
            client.create().forPath("/zk-huey/cnode");
        }
        client.setData().forPath("/zk-huey/cnode", "world".getBytes());

        Thread.sleep(10 * 1000);
        pool.shutdown();
        client.close();
    }
//因为节点“/curator”存在子节点，所以在删除的时候将会报错，事务回滚


    /**
     * EPHEMERAL_SEQUENTIAL 临时顺序节点
     * 在client写入数据后会按照档期那已经存在过的节点index 然后+1
     * 在client close 之后对应的节点的path和数据会被删除
     *
     */
    @Test
    void test_EPHEMERAL_SEQUENTIAL() throws Exception {
        CuratorFramework client = client();
//        client.create().forPath("/curator/EPHEMERAL_SEQUENTIAL/demo1");
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(
                "/curator/EPHEMERAL_SEQUENTIAL/demo1",
                "demo1 data".getBytes()
        );
        Thread.sleep(10000);
        client.close();
    }


    public CuratorFramework client(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR).connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
//命名空间           .namespace("super")
                .build();
        //3 开启连接
        client.start();
        return client;

    }

    /**
     * leader 选举
     */
    String ZK_PATH = "/zktest";
    @Test
    public void test_election() throws InterruptedException {


        new Thread(() -> {
            registerListener(getLeaderSelectorListener("线程111"));
        },"线程1").start();

        new Thread(() -> {
            registerListener(getLeaderSelectorListener("线程222"));
        },"线程2").start();

        new Thread(() -> {
            registerListener(getLeaderSelectorListener("线程333"));
        },"线程3").start();

        Thread.sleep(Integer.MAX_VALUE);
    }

    private LeaderSelectorListener getLeaderSelectorListener(String head) {
        LeaderSelectorListener listener = new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
               log.info("{} 成为了 master!",head);

                // takeLeadership() method should only return when leadership is being relinquished.
                Thread.sleep(5000L);

                log.info("{}  退出master!",head);
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState state) {
            }
        };
        return listener;
    }

    private void registerListener(LeaderSelectorListener listener) {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                CONNECT_ADDR,
                new RetryNTimes(10, 5000)
        );
        client.start();

        // 2.Ensure path
        try {
            new EnsurePath(ZK_PATH).ensure(client.getZookeeperClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3.Register listener
        LeaderSelector selector = new LeaderSelector(client, ZK_PATH, listener);
        selector.autoRequeue();
        selector.start();
    }
}
