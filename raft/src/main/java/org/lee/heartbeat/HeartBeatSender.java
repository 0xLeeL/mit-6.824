package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.TimerUtils;
import org.lee.election.Election;
import org.lee.rpc.Client;
import org.lee.rpc.RpcCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class HeartBeatSender {

    private final Context context;
    private final AtomicInteger failTimes = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);
    private Supplier<RpcCaller<String, String>> clientSupplier;
    private final GlobalConfig globalConfig;
    private final Election election;
    private Timer timer;


    public HeartBeatSender(Context context, GlobalConfig globalConfig, Election election) {
        this.context = context;
        this.globalConfig = globalConfig;
        this.clientSupplier = () -> new Client<>(globalConfig.getMasterHost(), globalConfig.getMasterPort()) {
            @Override
            public void onFailed() {
                log.info("send  to {}:{} ",globalConfig.getMasterHost(),globalConfig.getMasterPort());

                tryElect();
            }
        };
        this.election = election;
    }


    public void ping() {
        try {
            log.info("send  to {}:{} ",globalConfig.getMasterHost(),globalConfig.getMasterPort());
            RpcCaller<String, String> client = clientSupplier.get();
            client.connect();
            String call = client.call(Constant.HEART_BEAT_PATH, Constant.HEART_REQ, String.class);
            health(call);
            client.close();
        } catch (Throwable e) {
            log.error(e.getMessage(),e);
            log.info("ping failed");
        }
    }

    public void health(String call) {
        if (Constant.HEART_RESP.equals(call)) {
            context.health();
            log.info("master health...");
            failTimes.set(0);
        }
    }

    public void schedule() {
        timer = TimerUtils.schedule(this::ping, 100, globalConfig.getPingSeg());
    }

    void tryElect() {
        int fail = failTimes.incrementAndGet();
        log.info("send  to {}:{} ",globalConfig.getMasterHost(),globalConfig.getMasterPort());
        log.info("failed {} times", fail);
        if (fail >= globalConfig.getRetryTimes() && context.masterIsHealth()) {
            context.setMasterStatus(MasterStatus.SUSPEND);
            election.elect();
        }
    }


    public void setClientSupplier(Supplier<RpcCaller<String, String>> clientSupplier) {
        this.clientSupplier = clientSupplier;
    }

    public void stop(){
        if (timer!=null) {
            timer.cancel();
        }
    }
}
