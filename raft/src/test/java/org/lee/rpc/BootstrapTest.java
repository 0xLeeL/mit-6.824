package org.lee.rpc;

import org.junit.jupiter.api.Test;
import org.lee.boot.Bootstrap;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.ThreadUtil;
import org.lee.heartbeat.HeartBeatSender;

public class BootstrapTest {

    @Test
    void test_startServer(){
        Global global = new Global();
        Server server = new Bootstrap().startServer();
        HeartBeatSender heartBeatSender = new HeartBeatSender(global, new GlobalConfig());
        heartBeatSender.schedule();
//        ThreadUtil.sleep(4000);
        server.close();
    }
}
