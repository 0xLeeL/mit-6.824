package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.domain.ActorStatusEntry;
import org.lee.election.domain.CurrentActor;
import org.lee.election.domain.ProposeResult;
import org.lee.election.handler.ElectionHandler;
import org.lee.election.handler.SyncStatusHandler;
import org.lee.heartbeat.MasterStatus;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        server.register(Constant.MASTER_STATUS_SYNC, new SyncStatusHandler(globalConfig,global));
    }


    /**
     * @return
     */
    public CurrentActor elect() {
        while (true) {
            CurrentActor currentActor = doElect();
            if (CurrentActor.CANDIDATE != currentActor) {
                return currentActor;
            }
        }
    }

    private CurrentActor doElect() {

        global.addEpoch();
        int acceptedNum = proposes();
        log.info("{}'s accepted proposes num is {} ",
                globalConfig.getCurrentAddr(),
                acceptedNum);
        boolean majority = isMajority(acceptedNum);
        if (majority) {
            global.updateActor(CurrentActor.MASTER);
            syncLog();
            return CurrentActor.MASTER;
        }
        if (MasterStatus.HEALTH.equals(global.getMasterStatus())) {
            return CurrentActor.FOLLOWER;
        }
        return CurrentActor.CANDIDATE;
    }

    private void syncLog() {
        log.info("{} start to sync log", globalConfig.getCurrentAddr());
        List<SyncResult> syncResults = global.getEndpoints().parallelStream()
                .map(endpoint -> endpoint.syncStatus(
                        new ActorStatusEntry(
                                globalConfig.getCurrentHost(),
                                globalConfig.getCurrentPort(),
                                CurrentActor.MASTER.name()
                        )
                )).toList();
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
                .count()
                + 1; // 投票给自己
    }

    private boolean isMajority(int num) {
        return global.isMajority(num);
    }


    public Server getServer() {
        return server;
    }
}
