package org.lee.study;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterElection {
    private Logger log = LoggerFactory.getLogger(MasterElection.class);
    private static final String CONNECT_ADDR = "172.16.214.101:2181";

    static String ZK_PATH = "/zkElection";

    CuratorFramework client;
    LeaderSelector selector;
    /**
     * Curator 提现了一个名为 Leader Election 的模式，用于实现 Master 选举。其核心逻辑如下：
     * <p>
     * (1) 创建临时顺序节点
     * 每个参与选举的节点会在 ZooKeeper 中创建一个临时顺序节点（Ephemeral Sequential Node），通常位于一个共享的父路径下，例如 /election/。
     * 例如，节点 A、B、C 分别创建了 /election/node_0000000001、/election/node_0000000002 和 /election/node_0000000003。
     * (2) 获取子节点列表
     * 每个节点会获取父路径下的所有子节点列表，并根据节点名称的字典序进行排序。
     * 排序后，每个节点可以确定自己在当前列表中的位置。
     * (3) 确定 Leader
     * 如果某个节点发现自己的节点是列表中的第一个节点，则认为自己是当前的 Leader（Master）。
     * 否则，该节点会监听前一个节点的变化（Watch Mechanism），以便在前一个节点失效时重新检查是否成为新的 Leader。
     * (4) 处理节点失效
     * 如果当前 Leader 节点失效（例如由于网络分区或进程崩溃），其对应的临时节点会被 ZooKeeper 自动删除。
     * 其他节点会收到 Watch 事件通知，并重新检查子节点列表，确定新的 Leader。
     */
    public void startElection(String head) {
        registerListener(getLeaderSelectorListener(head));
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            client.close();
            selector.close(); // 主动释放 Leadership
            System.out.println("主动释放主动释放主动释放主动释放主动释放主动释放");
        }));
    }

    public LeaderSelectorListener getLeaderSelectorListener(String head) {
        LeaderSelectorListener listener = new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                log.info("{} 成为了 master!", head);

                // takeLeadership() method should only return when leadership is being relinquished.
                Thread.sleep(200L);

                log.info("{}  退出master!", head);
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState state) {
                log.error("state 改变了：{}", state);
            }
        };
        return listener;
    }

    public void registerListener(LeaderSelectorListener listener) {
        // 1.Connect to zk
        client = CuratorFrameworkFactory.newClient(
                CONNECT_ADDR,
                new RetryNTimes(10, 1000)
        );
        client.start();

        // 2.Ensure path
        try {
            new EnsurePath(ZK_PATH).ensure(client.getZookeeperClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3.Register listener
        selector = new LeaderSelector(client, ZK_PATH, listener);
        selector.autoRequeue();
        selector.start();
    }
}