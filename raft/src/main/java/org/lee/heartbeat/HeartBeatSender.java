package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.TimerUtils;
import org.lee.election.Election;
import org.lee.rpc.Client;
import org.lee.rpc.RpcCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class HeartBeatSender {

    private final Global global;
    private final AtomicInteger failTimes = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);
    private final Supplier<RpcCaller<String,String>> clientSupplier;
    private final GlobalConfig globalConfig;

    public HeartBeatSender(Global global, GlobalConfig globalConfig) {
        this.global = global;
        this.globalConfig = globalConfig;
        this.clientSupplier = () -> new Client<>(globalConfig.getMasterHost(), globalConfig.getMasterPort()) {
            @Override
            public void onFailed() {
                tryElect();
            }
        };
    }

    public HeartBeatSender(Global global, Supplier<RpcCaller<String,String>> clientSupplier, GlobalConfig globalConfig) {
        this.global = global;
        this.globalConfig = globalConfig;
        this.clientSupplier = clientSupplier;
    }

    public void ping() {
        RpcCaller<String,String> client = clientSupplier.get();
        client.connect();
        String call = client.call(Constant.HEART_BEAT_PATH, Constant.HEART_REQ, String.class);
        health(call);
        client.close();
        log.info("master health...");
    }

    public void health(String call) {
        if (Constant.HEART_RESP.equals(call)) {
            global.health();
        }
        failTimes.set(0);
    }

    public void schedule() {
        TimerUtils.schedule(this::ping, 1000, globalConfig.getPingSeg());
    }

    public void tryElect() {
        int fail = failTimes.incrementAndGet();
        log.info("failed {} times", fail);
        if (fail >= globalConfig.getRetryTimes()) {
            new Election(global, global.getServer()).elect();
        }
    }
}
