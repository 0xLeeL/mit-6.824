package org.lee;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Main2 {

    static Set<Thread> threads = new HashSet<>();
    public static void main(String[] args) throws InterruptedException, IOException {

        byte[] bytes = "a".getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = "xxxxxx".getBytes(StandardCharsets.UTF_8);
        CompletableFuture.runAsync(()->{
            try {
                ServerSocket serverSocket = new ServerSocket(80);
                Socket accept = serverSocket.accept();
                byte[] bytes1 = accept.getInputStream().readNBytes(bytes.length);
                System.out.println(new String(bytes1));
                accept.getOutputStream().write(bytes2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",80));
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        byte[] bytes1 = new byte[bytes2.length];
        dataInputStream.readFully(bytes1);
        System.out.println("client:"+new String(bytes1));

    }
}
