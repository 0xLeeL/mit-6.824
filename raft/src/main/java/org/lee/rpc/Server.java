package org.lee.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 *  The class listen command from client(or other server),and response result
 */
public class Server {
    private final Logger log = LoggerFactory.getLogger(Server.class);
    private final ServerSocket serverSocket;

    private final int port;
    public Server(int  serverPort) {
        try {
            this.port = serverPort;
            this.serverSocket = new ServerSocket(serverPort);
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("rpc服务端启动成功");
                while (true) {
                    Socket accept = serverSocket.accept();
                    process(accept);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void process(Socket accept){

        InputStream inputStream = accept.getInputStream();
        inputStream.read



//    AllBytes()
    }

}
