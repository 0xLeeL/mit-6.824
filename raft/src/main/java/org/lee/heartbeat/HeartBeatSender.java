package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.TimerUtils;
import org.lee.election.Election;
import org.lee.election.ElectionRaft;
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
    private Supplier<RpcCaller<String,String>> clientSupplier;
    private final GlobalConfig globalConfig;
    private final Election election;

    public HeartBeatSender(Global global, GlobalConfig globalConfig, Election election) {
        this.global = global;
        this.globalConfig = globalConfig;
        this.clientSupplier = () -> new Client<>(globalConfig.getMasterHost(), globalConfig.getMasterPort()) {
            @Override
            public void onFailed() {
                tryElect();
            }
        };
        this.election = election;
    }


    public void ping() {
        RpcCaller<String,String> client = clientSupplier.get();
        client.connect();
        String call = client.call(Constant.HEART_BEAT_PATH, Constant.HEART_REQ, String.class);
        health(call);
        client.close();
    }

    public void health(String call) {
        if (Constant.HEART_RESP.equals(call)) {
            global.health();
            log.info("master health...");
            failTimes.set(0);
        }
    }

    public void schedule() {
        TimerUtils.schedule(this::ping, 1000, globalConfig.getPingSeg());
    }

    public void tryElect() {
        int fail = failTimes.incrementAndGet();
        log.info("failed {} times", fail);
        if (fail >= globalConfig.getRetryTimes()) {
            new ElectionRaft(global, globalConfig).elect();
        }
    }

    public int getFailTimes() {
        return failTimes.get();
    }

    public void setClientSupplier(Supplier<RpcCaller<String, String>> clientSupplier) {
        this.clientSupplier = clientSupplier;
    }
}
