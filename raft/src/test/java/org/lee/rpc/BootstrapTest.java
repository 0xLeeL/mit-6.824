package org.lee.rpc;

import org.junit.jupiter.api.Test;
import org.lee.Bootstrap;
import org.lee.common.Global;
import org.lee.common.utils.ThreadUtil;
import org.lee.hearbeat.HeartBeatSender;

public class BootstrapTest {

    @Test
    void test_startServer(){
        Global global = new Global();
        Server server = new Bootstrap().startServer();
        HeartBeatSender heartBeatSender = new HeartBeatSender();
        heartBeatSender.setGlobal(global);
        heartBeatSender.schedule();
        ThreadUtil.sleep(4000);
        server.close();
    }
}
