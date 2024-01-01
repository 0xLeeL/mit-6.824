package org.lee.lab1;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Worker {
    private int port;

    public Worker(int port) {
        this.port = port;
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddr() {
        return "http://localhost:" + port;
    }
}
