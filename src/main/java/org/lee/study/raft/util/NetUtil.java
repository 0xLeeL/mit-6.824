package org.lee.study.raft.util;

import lombok.extern.slf4j.Slf4j;
import org.lee.study.raft.NetAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
public class NetUtil {

    public static void sendMessageByUdp(NetAddress address, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.connect(new InetSocketAddress(address.getHostName(), address.getPort()));
        socket.send(new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), 0, message.length()));
        socket.close();
    }

    public static void sendMessageByTcp(NetAddress address, String message) throws IOException {
        Socket socket = new Socket(address.getHostName(), address.getPort());
        sendMessageByTcp(socket, message);
    }

    public static void sendMessageByTcp(Socket socket, String message) throws IOException {
        try (OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }


    public static void tcp(int port, Consumer<String> consumer) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("start to listen port:{} !!!", port);
            while (true) {
                Socket accept = serverSocket.accept();
                InputStream inputStream = accept.getInputStream();
                byte[] buffer = new byte[1024];
                int len = inputStream.read(buffer);
                if (len == -1) {
                    log.info("listening is ended!!!");
                    break;
                }
                consumer.accept(StringUtil.to(buffer, 0, len));
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void udp(int port){
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            log.info("start to listen port:{} !!!", port);
            byte[] data = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length);
            while (true) {
                int length = datagramPacket.getLength();
                if (length == -1 || length == 0) {
                    log.info("listening is ended!!!");
                    break;
                }
                datagramSocket.receive(datagramPacket);
                log.info(StringUtil.to(data, 0, length));
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
