package org.lee.study.raft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class NetUtil {

    public static void sendMessageByUdp(InetSocketAddress address, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.connect(address);
        socket.send(new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),0,message.length()));
        socket.close();
    }

    public static void sendMessageByTcp(InetSocketAddress address, String message) throws IOException {
        Socket socket = new Socket(address.getAddress().getHostAddress(),address.getPort());
        sendMessageByTcp(socket, message);
    }

    public static void sendMessageByTcp(Socket socket , String message) throws IOException {
        try(OutputStream outputStream = socket.getOutputStream()){
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }


    public static void tcp(int port) throws IOException{

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("启动成功");
        while (true){
            Socket accept = serverSocket.accept();
            InputStream inputStream = accept.getInputStream();
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            if (len == -1){
                System.out.println("结束");
                break;
            }
            String s = new String(buffer, 0, len, StandardCharsets.UTF_8);
            System.out.println(s);
        }
    }

    public static void udp(int port) throws IOException{

        DatagramSocket datagramSocket = new DatagramSocket(port);
        System.out.println("启动成功");
        byte[] data = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length);
        while(true){
            int length = datagramPacket.getLength();
            if (length == -1 || length == 0){
                break;
            }
            datagramSocket.receive(datagramPacket);
            System.out.println(StringUtil.to(data,0, length));
        }
    }
}
