package org.lee.lab1;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.SocketUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Worker<RESULT extends Serializable, ARG> {
    private int port;
    private int master;
    private final ServerSocket serverSocket;

    public Worker(int masterPort, int port) {
        log.info("worker：{}", port);
        this.port = port;
        this.master = masterPort;
        try {
            serverSocket = new ServerSocket(port);
            register();
            read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void read() {

        try {
            while (true) {
                log.info("start 监听任务");
                Socket socket = serverSocket.accept();
                InputStream stream = socket.getInputStream();
                Task<RESULT, ARG> o = (Task<RESULT, ARG>) SocketUtil.readObject(stream);
//                socket.close();
                log.info("receive task :{}", o);
                RESULT run = o.run();
                log.info("worker result is :{}", run);
                SocketUtil.objectSend(run, socket.getOutputStream());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void register() {
        CompletableFuture.runAsync(() -> {
            while (true){
                try {
                    log.info("start register ");
                    SocketUtil.objectSend(port, master);
                    log.info("register successfully");
                    return;
                } catch (Exception e) {
                    log.info("register fail retry after ");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
