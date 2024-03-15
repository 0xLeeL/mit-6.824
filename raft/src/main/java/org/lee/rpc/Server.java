package org.lee.rpc;


import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.lee.rpc.common.RpcUtil.readToString;

/**
 * The class listen command from client(or other server),and response result
 */
public class Server {

    private static Server  instance = null;
    public static Server getInstance(){
        return instance;
    }

    public static void setInstance(Server instance) {
        Server.instance = instance;
    }



    private final Logger log = LoggerFactory.getLogger(Server.class);
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor poolExecutor;
    private final Dispatcher dispatcher = new Dispatcher();

    {
        int processors = Runtime.getRuntime().availableProcessors();
        int maxConnectSize = processors << 10;
        poolExecutor = new ThreadPoolExecutor(
                processors << 1,

                processors << 2,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxConnectSize),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private final int port;

    public Server(int serverPort) {
        try {
            this.port = serverPort;
            this.serverSocket = new ServerSocket(port);
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> listen() {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("server started listen on :{} ", port);
                while (true) {
                    Socket accept = serverSocket.accept();
                    process(accept);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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
        log.info("server receive:{}", reqJson);
        Object dispatch = dispatcher.dispatch(path, reqJson);
        RpcUtil.sendObj(dispatch, outputStream);
    }

    public void register(String path, Handler handler) {
        log.info("register:{}", path);
        dispatcher.register(path, handler);
    }


}
