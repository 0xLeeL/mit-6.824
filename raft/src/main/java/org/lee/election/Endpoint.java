package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.domain.Propose;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.Client;

import java.util.Comparator;
import java.util.Objects;

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
        Client client = new Client(host, port);
        client.connect();
        return client.call(
                Constant.ELECTION_PATH,
                new Propose(global.getEpoch(), globalConfig.getCurrentPort()),
                ProposeResult.class
        );
        // TODO(Lee): handle the result
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Endpoint e){
            return (host + port).compareTo(e.host() + e.port());
        }
        throw new RuntimeException("type error");
    }
}
