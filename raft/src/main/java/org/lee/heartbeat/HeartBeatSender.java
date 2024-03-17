package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.TimerUtils;
import org.lee.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatSender {

    private final Global global;
    private final GlobalConfig globalConfig;

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);

    public HeartBeatSender(Global global, GlobalConfig globalConfig){
        this.global = global;
        this.globalConfig = globalConfig;
    }

    public void ping() {
        Client client = new Client(globalConfig.getMasterHost(), globalConfig.getMasterPort());
        client.connect();
        String call = client.call(Constant.HEART_BEAT_PATH, Constant.HEART_REQ, String.class);
        if (Constant.HEART_RESP.equals(call)){
            global.health();
        }
        log.info("master health...");
    }

    public void schedule() {
        TimerUtils.schedule(this::ping, 1000, 2000);
    }
}
