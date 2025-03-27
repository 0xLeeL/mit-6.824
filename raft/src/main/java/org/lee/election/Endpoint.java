package org.lee.election;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Constant;
import org.lee.election.domain.*;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.factory.ClientFactory;

/**
 * This class record the all server in the cluster.
 */
@Slf4j
public record Endpoint(
        int port,
        String host,
        String status
) implements Comparable<Endpoint> {
    public Endpoint(int port, String host){
        this(port,host,"");
    }

    public String addr(){
        return host + ":" + port;
    }
    /**
     * launch an election request
     */

    public ProposeResult propose(int epoch, int currentPort) {
        return call(
                Constant.ELECTION_PATH,
                new Propose(epoch, currentPort),
                ProposeResult.class
        );
    }


    public NodeUpdateResult updateNode(NodeUpdate nodeUpdate) {
        return call(
                Constant.NODE_UPDATE,
                nodeUpdate,
                NodeUpdateResult.class
        );
    }

    public SyncResult sendLog(LogEntry logEntry) {
        return call(
                Constant.LOG_SYNC_PATH,
                logEntry,
                SyncResult.class
        );
    }

    public SyncResult rollback(LogEntry logEntry) {
        return call(
                Constant.LOG_SYNC_PATH,
                logEntry,
                SyncResult.class
        );
    }

    /**
     * Tell all servers, election has been done.
     * 同步master状态给这个endpoint， 告知其已经选举完成
     *
     * @param actorStatus
     * @return
     */
    public SyncResult syncStatus(ActorStatusEntry actorStatus) {
        return call(
                Constant.MASTER_STATUS_SYNC,
                actorStatus,
                SyncResult.class);
    }

    public  <R, T> R call(String path, T req, Class<R> cls) {
        RpcCaller<T, R> client = ClientFactory.ofNetty(host, port);
        client.connect();
        R call = client.call(
                path,
                req,
                cls
        );
        client.close();
        return call;
    }

    public String info() {
        return host + ":" + port;
    }

    @Override
    public int compareTo(Endpoint e) {
        return (host + port).compareTo(e.host() + e.port());
    }


    public static Endpoint build(String addr) {
        String[] split = addr.split(":");
        return new Endpoint(Integer.parseInt(split[1]), split[0], CurrentActor.NEW_NODE.name());
    }

}
