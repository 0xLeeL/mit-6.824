package org.lee.rpc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.utils.ThreadUtil;
import org.lee.rpc.socket.ClientSocket;
import org.lee.rpc.socket.ServerSocketImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RpcCallTest {
    private final Logger log = LoggerFactory.getLogger(RpcCallTest.class);
    @AfterEach
    void after(){

    }
    @Test
    void test_call() throws Exception {
        int port = 80;
        String path = "aaa";
        String callCommend = "call commend";
        String result = "call result";
        Server server = new ServerSocketImpl(port);
        server.register(path,  requestJson -> result);
        ClientSocket<String,String> clientSocket = new ClientSocket<>("localhost",port);
        clientSocket.connect();
        String call = clientSocket.call(path, callCommend, String.class);
        Assertions.assertEquals(result,call);
        server.close();
    }
    @Test
    void test_call_timeout() throws Exception {
        int port = 80;
        String path = "aaa";
        Server server = new ServerSocketImpl(port);
        server.register(path, requestJson -> {
            ThreadUtil.sleep(1_000);
            return "result";
        });
        AtomicBoolean timeouted = new AtomicBoolean(false);
        ClientSocket<String,String> clientSocket = new ClientSocket<>("localhost",port, RpcConfig.builder().timeoutMill(100));
        clientSocket.setSendFail(()->{
            log.info("setSendFail");
            timeouted.set(true);
        });
        clientSocket.connect();
        String call = clientSocket.call(path, "callCommend", String.class);
        Assertions.assertNull(call);
        Assertions.assertTrue(timeouted.get());
        server.close();
    }
}
