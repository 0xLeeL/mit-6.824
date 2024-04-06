package org.lee.election;

import org.lee.common.Constant;
import org.lee.common.Context;
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
import java.util.Optional;
import java.util.Random;


public class ElectionRaft implements Election {

    private static final Logger log = LoggerFactory.getLogger(Endpoint.class);
    private final Context context;
    private final GlobalConfig globalConfig;

    public ElectionRaft(Context context, GlobalConfig globalConfig) {
        this.context = context;
        this.globalConfig = globalConfig;
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

    /**
     * 选举发起
     * 1. 如果以及存在master那么直接转化为follower
     * 2. 否则继续选举知道选举出新的master为止
     *
     * @return 选举结果
     */
    private CurrentActor doElect() {
        ThreadUtil.sleep(new Random().nextInt(150) + 150);
        if (MasterStatus.HEALTH.equals(context.getMasterStatus())) {
            return CurrentActor.FOLLOWER;
        }

        context.addEpoch();
        int acceptedNum = 0;

        for (Endpoint endpoint : context.getEndpoints()) {
            if (endpoint.port() == globalConfig.getCurrentPort()
                    && globalConfig.getCurrentHost().equals(endpoint.host())) {
                acceptedNum++;
            }
            Optional<ProposeResult> proposeOpt = propose(endpoint);
            if (proposeOpt.isEmpty()) {
                continue;
            }
            ProposeResult propose = proposeOpt.get();
            if (propose.accept()) {
                acceptedNum++;
                continue;
            }
            if (propose.msg().existMaster()) {
                return CurrentActor.FOLLOWER;
            }
        }
        if (isMajority(acceptedNum)) {
            syncStatus();
            return CurrentActor.MASTER;
        }

        return CurrentActor.CANDIDATE;
    }

    private void syncStatus() {
        log.info("{} start to sync log", globalConfig.getCurrentAddr());
        List<SyncResult> syncResults = context.getEndpoints().parallelStream()
                .map(endpoint -> {
                    try {
                        SyncResult syncResult = endpoint.syncStatus(
                                new ActorStatusEntry(
                                        globalConfig.getCurrentHost(),
                                        globalConfig.getCurrentPort(),
                                        CurrentActor.MASTER.name()
                                )
                        );
                        return Optional.ofNullable(syncResult);
                    } catch (Exception e) {
                        log.error("sync status failed:{}", endpoint);
                        return Optional.<SyncResult>empty();
                    }
                }).filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<ProposeResult> propose(Endpoint endpoint) {
        try {
            ProposeResult propose = endpoint.propose(context.getEpoch(), globalConfig.getCurrentPort());
            log.info("propose result is :{}", propose);
            return Optional.ofNullable(propose);
        } catch (Exception e) {
            log.error("propose failed:{}", endpoint);
        }
        return Optional.empty();
    }

    private boolean isMajority(int num) {
        return context.isMajority(num);
    }

    public void register(Server server) {
        server.register(Constant.ELECTION_PATH, new ElectionHandler(context));
        server.register(Constant.MASTER_STATUS_SYNC, new SyncStatusHandler(server));
    }
}
