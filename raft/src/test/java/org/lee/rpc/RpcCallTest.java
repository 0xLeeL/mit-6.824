package org.lee.rpc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.utils.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RpcCallTest {
    private final Logger log = LoggerFactory.getLogger(RpcCallTest.class);
    @AfterEach
    void after(){

    }
    @Test
    void test_call() {
        int port = 80;
        String path = "aaa";
        String callCommend = "call commend";
        String result = "call result";
        Server server = new Server(port);
        server.register(path,  requestJson -> result);
        Client client = new Client("localhost",port);
        client.connect();
        String call = client.call(path, callCommend, String.class);
        Assertions.assertEquals(result,call);
        server.close();
    }
    @Test
    void test_call_timeout() {
        int port = 80;
        String path = "aaa";
        Server server = new Server(port);
        server.register(path, requestJson -> {
            ThreadUtil.sleep(1_000);
            return "result";
        });
        AtomicBoolean timeouted = new AtomicBoolean(false);
        Client client = new Client("localhost",port, RpcConfig.builder().timeoutMill(100));
        client.setSendFail(()->{
            log.info("setSendFail");
            timeouted.set(true);
        });
        client.connect();
        String call = client.call(path, "callCommend", String.class);
        Assertions.assertNull(call);
        Assertions.assertTrue(timeouted.get());
        server.close();
    }
}
