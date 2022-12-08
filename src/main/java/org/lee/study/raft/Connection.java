package org.lee.study.raft;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
    public static void main(String[] args) throws Throwable {
        ServerSocket serverSocket = new ServerSocket(8080);
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.accept();
    }
}
