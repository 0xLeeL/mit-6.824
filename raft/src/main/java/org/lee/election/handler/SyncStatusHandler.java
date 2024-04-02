package org.lee.election.handler;

import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.JsonUtil;
import org.lee.election.domain.ActorStatusEntry;
import org.lee.election.domain.CurrentActor;
import org.lee.heartbeat.MasterStatus;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncStatusHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(SyncStatusHandler.class);
    private final GlobalConfig globalConfig;
    private final Context context;

    public SyncStatusHandler(Server server) {
        this.globalConfig = server.getGlobalConfig();
        this.context = server.getContext();
    }

    @Override
    public Object handle(String requestJson) {
        ActorStatusEntry request = JsonUtil.fromJson(requestJson, ActorStatusEntry.class);
        boolean sameServer = request.sameServer(globalConfig.getCurrentHost(), globalConfig.getCurrentPort());
        if (!sameServer) {
            context.setMasterStatus(MasterStatus.HEALTH);
            context.updateActor(CurrentActor.FOLLOWER);
            globalConfig.setMasterHost(request.host());
            globalConfig.setMasterPort(request.port());
            log.info("election finished, become a actor:{}, master status is :{}", context.getCurrentActor(), context.getMasterStatus());
        } else {
            log.info("election finished, become a actor:{}, master status is :{}", context.getCurrentActor(), context.getMasterStatus());
        }
        return SyncResult.success();
    }
}
