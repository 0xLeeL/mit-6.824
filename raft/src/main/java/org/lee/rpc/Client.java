package org.lee.rpc;

import org.lee.common.utils.JsonUtil;
import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final String host;
    private final Integer port;
    private final Socket socket;

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
        this.socket = new Socket();
    }


    public <T, R> R call(String path, T commend, Class<R> resultClass) {
        try (OutputStream outputStream = socket.getOutputStream()) {
            RpcUtil.sendString(path, outputStream);
            RpcUtil.sendObj(commend, outputStream);
            InputStream inputStream = socket.getInputStream();
            String s = RpcUtil.readToString(inputStream);
            log.info("call result is :{}", s);
            return JsonUtil.fromJson(s, resultClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
