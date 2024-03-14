package org.lee.rpc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcCallTest {
    private final Logger log = LoggerFactory.getLogger(RpcCallTest.class);
    @Test
    void test_call(){
        int port = 80;
        String path = "aaa";
        String callCommend = "call commend";
        String result = "call result";
        Server server = new Server(port);
        server.register(path, new Handler() {
            @Override
            public String handle(String requestJson) {
                return result;
            }
        });
        Client client = new Client("localhost",port);
        client.connect();
        String call = client.call(path, callCommend, String.class);
        Assertions.assertEquals(result,call);
    }
}
