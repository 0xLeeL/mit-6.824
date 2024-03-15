package org.lee.rpc;

import org.junit.jupiter.api.Test;
import org.lee.Bootstrap;
import org.lee.common.utils.ThreadUtil;
import org.lee.hearbeat.HeartBeatSender;

public class BootstrapTest {

    @Test
    void test_startServer(){
        new Bootstrap().startServer();
        HeartBeatSender.schedule();
        ThreadUtil.sleep(10000);
    }
}
