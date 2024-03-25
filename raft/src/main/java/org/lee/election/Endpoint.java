package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
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
        String host,
        Global global,
        GlobalConfig globalConfig
) implements Comparable {

    /**
     * launch an election request
     */
    public ProposeResult propose() {
        Client<Propose,ProposeResult> client = new Client<>(host, port);
        client.connect();
        return client.call(
                Constant.ELECTION_PATH,
                new Propose(global.getEpoch(), globalConfig.getCurrentPort()),
                ProposeResult.class
        );
    }

    public SyncResult sendLog(LogEntry logEntry) {
        Client<LogEntry,SyncResult> client = new Client<>(host, port);
        client.connect();
        return client.call(
                Constant.LOG_SYNC_PATH,
                logEntry,
                SyncResult.class
        );
    }

    /**
     * Tell all servers, election has been done.
     * 同步master状态给这个endpoint， 告知其已经选举完成
     * @param actorStatus
     * @return
     */
    public SyncResult syncStatus(ActorStatusEntry actorStatus) {
        Client<ActorStatusEntry,SyncResult> client = new Client<>(host, port);
        client.connect();
        return client.call(
                Constant.MASTER_STATUS_SYNC,
                actorStatus,
                SyncResult.class
        );
    }

    public String info() {
        return host + ":" + port;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Endpoint e) {
            return (host + port).compareTo(e.host() + e.port());
        }
        throw new RuntimeException("type error");
    }
}
