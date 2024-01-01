package org.lee.common;

import org.junit.jupiter.api.Test;
import org.lee.common.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class SocketUtilTest {

    @Test
    public void test_rpc() throws Exception {
        ServerSocket server0 = new ServerSocket(80);
        CompletableFuture.runAsync(() -> {
            try {
                Socket socket;
                while ((socket = server0.accept()) != null) {
                    InputStream stream = socket.getInputStream();
                    Object o = SocketUtil.readObject(stream);
                    System.out.println(o);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
//        ServerSocket server1 = new ServerSocket(81);
        Socket socket = new Socket("localhost", 80);
        SocketUtil.objectSend("a",socket.getOutputStream());
        InputStream inputStream = socket.getInputStream();
        Object o = SocketUtil.readObject(inputStream);
        System.out.println(o);
    }

    @Test
    void test_o(){
        System.out.println("xxx");
    }


}
