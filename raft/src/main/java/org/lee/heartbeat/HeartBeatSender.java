package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.TimerUtils;
import org.lee.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class HeartBeatSender {

    private final Global global;
    private final AtomicInteger failTimes = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);
    private final int failTimesOfStartToElection;
    private final Supplier<Client> clientSupplier;
    private final int pingSeq;

    public HeartBeatSender(Global global, GlobalConfig globalConfig) {
        this(
                global,
                globalConfig.getRetryTimes(),
                ()->new Client(globalConfig.getMasterHost(), globalConfig.getMasterPort()),
                globalConfig.getPingSeg()
        );
    }

    public HeartBeatSender(Global global, int failTimesOfStartToElection, Supplier<Client> clientSupplier, int pingSeq) {
        this.global = global;
        this.failTimesOfStartToElection = failTimesOfStartToElection;
        this.clientSupplier = clientSupplier;
        this.pingSeq = pingSeq;
    }

    public void ping() {
        Client client = clientSupplier.get();
        client.setSendFail(() -> {
            int failedTimes = failTimes.incrementAndGet();
            if (failedTimes > failTimesOfStartToElection) {
                startElection();
            }
        });
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
        TimerUtils.schedule(this::ping, 1000, pingSeq);
    }

    public void startElection() {
        // TODO(Lee): start to elect
        // push event ?
    }
}
