package org.lee.election.handler;

import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.JsonUtil;
import org.lee.election.domain.ActorStatusEntry;
import org.lee.election.domain.CurrentActor;
import org.lee.heartbeat.MasterStatus;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncStatusHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(SyncStatusHandler.class);
    private final GlobalConfig globalConfig;
    private final Global global;

    public SyncStatusHandler(GlobalConfig globalConfig, Global global) {
        this.globalConfig = globalConfig;
        this.global = global;
    }

    @Override
    public Object handle(String requestJson) {
        ActorStatusEntry request = JsonUtil.fromJson(requestJson, ActorStatusEntry.class);
        boolean sameServer = request.sameServer(globalConfig.getCurrentHost(), globalConfig.getCurrentPort());
        if (!sameServer){
            global.setMasterStatus(MasterStatus.HEALTH);
            global.updateActor(CurrentActor.FOLLOWER);
            log.info("election finished, become a actor:{}, master status is :{}",global.getCurrentActor(),global.getMasterStatus());
        }else{
            log.info("election finished, become a actor:{}, master status is :{}",global.getCurrentActor(),global.getMasterStatus());
        }
        return SyncResult.success();
    }
}
