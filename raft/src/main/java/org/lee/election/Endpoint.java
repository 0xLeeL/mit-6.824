package org.lee.election;

import org.lee.common.Constant;
import org.lee.election.domain.ActorStatusEntry;
import org.lee.election.domain.Propose;
import org.lee.election.domain.ProposeResult;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Client;

/**
 * This class record the all server in the cluster.
 */
public record Endpoint(
        int port,
        String host
) implements Comparable<Endpoint> {

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

    public SyncResult sendLog(LogEntry logEntry) {
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

    private <R, T> R call(String path, T req, Class<R> cls) {
        Client<T, R> client = new Client<>(host, port);
        client.connect();
        return client.call(
                path,
                req,
                cls
        );
    }

    public String info() {
        return host + ":" + port;
    }

    @Override
    public int compareTo(Endpoint e) {
            return (host + port).compareTo(e.host() + e.port());

    }
}
