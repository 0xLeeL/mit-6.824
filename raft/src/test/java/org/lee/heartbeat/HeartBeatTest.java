package org.lee.heartbeat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.boot.Bootstrap;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.Election;
import org.lee.heartbeat.handler.HeartBeatHandler;
import org.lee.rpc.Client;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;


public class HeartBeatTest {
    private final Logger log = LoggerFactory.getLogger(HeartBeatTest.class);

    @Test
    void test_ping() {
        Context context = new Context();
        Server server = new Bootstrap().startServer();
        HeartBeatReceiver receiver = new HeartBeatReceiver(server);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicInteger heartbeatTimes = new AtomicInteger(0);
        receiver.startListenHeartBeat(new HeartBeatHandler(context) {
            @Override
            public String handle(String requestJson) {
                atomicBoolean.set(true);
                heartbeatTimes.incrementAndGet();
                return super.handle(requestJson);
            }
        });
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setPingSeg(100);
        HeartBeatSender heartBeatSender = new HeartBeatSender(context, globalConfig, null);
        heartBeatSender.schedule();
        ThreadUtil.sleep(1520);
        Assertions.assertTrue(atomicBoolean.get());
        Assertions.assertTrue(heartbeatTimes.get() >= 5);
        server.close();
    }

    @Test
    void test_ping_timeout() {
        Context context = new Context();
        context.setMasterStatus(MasterStatus.HEALTH);
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setPingSeg(50);
        Election election = mock(Election.class);
        HeartBeatSender heartBeatSender = new HeartBeatSender(context, globalConfig, election);

        RpcCaller<String, String> rpcCaller = new Client<>("", 0) {
            @Override
            public String call(String path, String commend, Class<String> resultClass) {
                onFailed();
                return "";
            }

            @Override
            public void connect() {

            }

            @Override
            public void onFailed() {
                heartBeatSender.tryElect();
            }
        };
        heartBeatSender.setClientSupplier(() -> rpcCaller);

        heartBeatSender.schedule();
        ThreadUtil.sleep(200);

        verify(election,times(1)).elect();


    }
}
