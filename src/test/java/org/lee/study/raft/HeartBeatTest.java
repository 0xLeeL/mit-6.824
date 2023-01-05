package org.lee.study.raft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.lee.study.raft.heartbeat.HeartBeat;
import org.lee.study.raft.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HeartBeatTest {
    private final Logger log = LoggerFactory.getLogger(HeartBeatTest.class);
    int client1 = 8081;
    int client2 = 8082;
    int client3 = 8083;

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void test_beat() throws InterruptedException {
        CountDownLatch cdlC1 = new CountDownLatch(3);
        CountDownLatch cdlC2 = new CountDownLatch(3);
        CountDownLatch cdlC3 = new CountDownLatch(3);
        // server
        new Thread(() -> {
            HeartBeat heartBeat = new HeartBeat();
            heartBeat.addAll(List.of(
                    NetAddress.localAddr(client1),
                    NetAddress.localAddr(client2),
                    NetAddress.localAddr(client3)
            ));
            heartBeat.startUp();
        }).start();

        new Thread(() -> NetUtil.tcp(
                client1,
                msg -> {
                    log.info("client:{}, received:{}", client1, msg);
                    cdlC1.countDown();
                }
        )).start();
        new Thread(() -> NetUtil.tcp(
                client2,
                msg -> {
                    log.info("client:{}, received:{}", client2, msg);
                    cdlC2.countDown();
                }
        )).start();
        new Thread(() -> NetUtil.tcp(
                client3,
                msg -> {
                    log.info("client:{}, received:{}", client3, msg);
                    cdlC3.countDown();
                }
        )).start();

        cdlC1.await();
        cdlC2.await();
        cdlC3.await();
    }
}
