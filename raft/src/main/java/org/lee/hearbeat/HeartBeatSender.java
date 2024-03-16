package org.lee.hearbeat;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatSender {

    private Global global;
    private GlobalConfig globalConfig = new GlobalConfig();

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);

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
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ping();
            }
        };

        // 定时任务开始执行后延迟1秒执行，之后每隔2秒执行一次
        timer.schedule(task, 1000, 2000);
    }

    public void setGlobal(Global global) {
        this.global = global;
    }
}
