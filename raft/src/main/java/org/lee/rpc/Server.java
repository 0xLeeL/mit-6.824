package org.lee.rpc;


import org.lee.common.Context;
import org.lee.common.GlobalConfig;
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
public class Server implements AutoCloseable {

    public static Server start() {
        return start(new GlobalConfig().getCurrentPort());
    }

    public static Server start(int port) {
        return new Server(port);
    }


    private final Logger log = LoggerFactory.getLogger(Server.class);

    private GlobalConfig globalConfig;
    private Context context;
    private ServerSocket serverSocket;
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


    public Server(int listenPort) {
        GlobalConfig config = new GlobalConfig();
        config.setCurrentPort(listenPort);
        init(config);
    }

    public Server(GlobalConfig globalConfig) {
        init(globalConfig);
    }

    public void init(GlobalConfig globalConfig) {
        try {
            this.globalConfig = globalConfig;
            int listenPort = globalConfig.getCurrentPort();
            this.serverSocket = new ServerSocket(listenPort);
            globalConfig.setCurrentPort(listenPort);
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> listen() {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("server started listen on :{} ", globalConfig.getCurrentPort());
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
        Object dispatch = dispatcher.dispatch(path, reqJson);
        RpcUtil.sendObj(dispatch, outputStream);
    }

    public void register(String path, Handler handler) {
        log.info("register:{}", path);
        dispatcher.register(path, handler);
    }


    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobal(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
