package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Election {

    private static final Logger log = LoggerFactory.getLogger(Endpoint.class);
    private Global global;
    private Server server;
    private GlobalConfig globalConfig;

    public Election(Global global, Server server) {
        this.global = global;
        this.server = server;
        this.globalConfig = server.getGlobalConfig();
        server.register(Constant.ELECTION_PATH, new ElectionHandler(global));
    }


    public void elect() {

        global.addEpoch();
        int acceptNum = (int) global.getEndpoints()
                .parallelStream()
                .filter(
                        endpoint -> !(endpoint.port() == globalConfig.getCurrentPort()
                                && globalConfig.getCurrentHost().equals(endpoint.host()))
                ).map(c -> {
                    ProposeResult propose = c.propose();
                    log.info("propose result is :{}", propose);
                    return propose;
                }).count();
        log.info("{}:{}'s accepted proposes num is {} ",
                globalConfig.getCurrentHost(),
                globalConfig.getCurrentPort(),
                acceptNum);
    }
}
