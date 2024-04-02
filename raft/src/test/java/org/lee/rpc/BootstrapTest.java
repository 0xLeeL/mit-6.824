package org.lee.rpc;

import org.junit.jupiter.api.Test;
import org.lee.boot.Bootstrap;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.heartbeat.HeartBeatSender;

public class BootstrapTest {

    @Test
    void test_startServer(){
        Context context = new Context();
        Server server = new Bootstrap().startServer();
        HeartBeatSender heartBeatSender = new HeartBeatSender(context, new GlobalConfig(), null);
        heartBeatSender.schedule();
//        ThreadUtil.sleep(4000);
        server.close();
    }
}
