package org.lee.rpc;

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
    private final RpcConfig config;
    private Runnable sendFail = ()->{};

    public Client(String host, Integer port) {
        this(host,port, new RpcConfig());
    }

    public Client(String host, Integer port, RpcConfig config) {
        this.host = host;
        this.port = port;
        this.socket = new Socket();
        this.config = config;
    }


    public <T, R> R call(String path, T commend, Class<R> resultClass) {
        try (OutputStream outputStream = socket.getOutputStream()) {
            RpcUtil.sendString(path, outputStream);
            RpcUtil.sendObj(commend, outputStream);
            socket.setSoTimeout(config.getTimeoutMill());
            InputStream inputStream = socket.getInputStream();
            R result = RpcUtil.readToObject(inputStream, resultClass);
            log.info("call result is :{}", result);
            return result;
        } catch (IOException e) {
            sendFail.run();
            return null;
        }
    }

    public <T> String call(String path, T commend) {
        return call(path, commend, String.class);
    }

    public void connect() {
        try {
            log.info("connect to {}:{}",host,port);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSendFail(Runnable sendFail) {
        this.sendFail = sendFail;
    }

    public void close(){
        if (socket!=null && !socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
