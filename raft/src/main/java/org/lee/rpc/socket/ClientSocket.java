package org.lee.rpc.socket;

import org.lee.rpc.RpcCaller;
import org.lee.rpc.RpcConfig;
import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket<T, R> implements RpcCaller<T, R> {
    private static final Logger log = LoggerFactory.getLogger(ClientSocket.class);

    private final String host;
    private final Integer port;
    private final Socket socket;
    private final RpcConfig config;
    private Runnable sendFail = () -> {
    };

    public ClientSocket(String host, Integer port) {
        this(host, port, new RpcConfig());
    }

    public ClientSocket(String host, Integer port, RpcConfig config) {
        this.host = host;
        this.port = port;
        this.socket = new Socket();
        this.config = config;
    }


    public R call(String path, T command, Class<R> resultClass) {
        try (OutputStream outputStream = socket.getOutputStream()) {
            RpcUtil.sendRequest(path,command, outputStream);
            socket.setSoTimeout(config.getTimeoutMill());
            InputStream inputStream = socket.getInputStream();
            R result = RpcUtil.readToObject(inputStream, resultClass);
            log.info("call result is :{}", result);
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            onFailed();
            return null;
        }
    }

    public void connect() {
        try {
//            log.info("connect to {}:{}",host,port);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            onFailed();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailed() {
        sendFail.run();
    }

    public void setSendFail(Runnable sendFail) {
        this.sendFail = sendFail;
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
