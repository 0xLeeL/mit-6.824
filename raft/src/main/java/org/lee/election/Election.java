package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.domain.CurrentActor;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Election {

    private static final Logger log = LoggerFactory.getLogger(Endpoint.class);
    private final Global global;
    private final Server server;
    private final GlobalConfig globalConfig;

    public Election(Global global, Server server) {
        this.global = global;
        this.server = server;
        this.globalConfig = server.getGlobalConfig();
        server.register(Constant.ELECTION_PATH, new ElectionHandler(global));
    }


    public void elect() {

        global.addEpoch();
        int acceptedNum = proposes();
        log.info("{}'s accepted proposes num is {} ",
                globalConfig.getCurrentAddr(),
                acceptedNum);
        if (isMajority(acceptedNum)) {
            global.updateActor(CurrentActor.MASTER);
            syncLog();
        }

    }

    private void syncLog() {
        log.info("{} start to sync log", globalConfig.getCurrentAddr());
    }

    private int proposes() {
        // 为了阻止选票起初就被瓜分，选举超时时间是从一个固定的区间（例如 150-300 毫秒）随机选择。
        ThreadUtil.sleep(new Random().nextInt(150) + 150);
        return (int) global.getEndpoints()
                .parallelStream()
                .filter(
                        endpoint -> !(endpoint.port() == globalConfig.getCurrentPort()
                                && globalConfig.getCurrentHost().equals(endpoint.host()))
                ).map(c -> {
                    ProposeResult propose = c.propose();
                    log.info("propose result is :{}", propose);
                    return propose;
                })
                .filter(ProposeResult::accept)
                .count();
    }

    public boolean isMajority(int num) {
        return global.isMajority(num);
    }
}
