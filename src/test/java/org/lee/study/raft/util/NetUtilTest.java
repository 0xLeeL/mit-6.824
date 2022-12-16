package org.lee.study.raft.util;


import org.junit.jupiter.api.Test;
import org.lee.study.raft.NetAddress;

import java.io.IOException;



class NetUtilTest {

    @Test
    void test_tcp() throws InterruptedException {
        new Thread(() -> NetUtil.tcp(8080, System.out::println), "listener").start();
        new Thread(() -> {
            try {
                NetUtil.sendMessageByTcp(new NetAddress("localhost", 8080), "测试测试测试");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "sender").start();
        Thread.sleep(1000 * 10);
    }
}