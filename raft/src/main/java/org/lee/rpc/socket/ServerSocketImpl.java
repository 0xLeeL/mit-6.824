package org.lee.rpc.socket;


import lombok.extern.slf4j.Slf4j;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.ThreadUtil;
import org.lee.rpc.Request;
import org.lee.rpc.Response;
import org.lee.rpc.Server;
import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static org.lee.rpc.common.RpcUtil.readToString;

/**
 * The class listen command from client(or other server),and response result
 */
public class ServerSocketImpl extends Server {
    private final ThreadPoolExecutor poolExecutor = ThreadUtil.poolOfIO("rpc-server");


    private ServerSocket serverSocket;
    private final Logger log = LoggerFactory.getLogger(ServerSocketImpl.class);


    public ServerSocketImpl(int listenPort) {
        super(listenPort);
    }

    public ServerSocketImpl(GlobalConfig globalConfig, Context context) {
        super(globalConfig, context);
    }


    public void process(Socket accept) {
        CompletableFuture.runAsync(() -> {
            InputStream inputStream = null;
            try {
                inputStream = accept.getInputStream();
                String path = readToString(inputStream);
                deal(path, inputStream, accept.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, poolExecutor);
    }


    private void deal(String path, InputStream inputStream, OutputStream outputStream) throws IOException {
        String reqJson = readToString(inputStream);
        getDispatcher().dispatch(new Request(reqJson,path,
                new SocketResponse(outputStream)));
    }
    public CompletableFuture<Void> listen() {
        return CompletableFuture.runAsync(() -> {
            try {
                serverSocket = new ServerSocket();
                GlobalConfig config = getGlobalConfig();
                serverSocket.bind(new InetSocketAddress(config.getCurrentPort()));
                log.info("server started listen on :{} ", config.getCurrentPort());
                while (true) {
                    Socket accept = serverSocket.accept();
                    process(accept);
                }
            } catch (Exception e) {
                log.warn("server closed");
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Slf4j
    static class SocketResponse implements Response{
        private final OutputStream socket;

        public SocketResponse(OutputStream socket) {
            this.socket = socket;
        }

        @Override
        public void back(Object response) {
            try {
                RpcUtil.sendObj(response, socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
