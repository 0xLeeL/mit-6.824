package org.lee.rpc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lee.common.utils.JsonUtil;
import org.lee.rpc.Request;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ClientNetty<T, R> implements RpcCaller<T, R> {
    private static final Logger log = LoggerFactory.getLogger(ClientNetty.class);

    private final String host;
    private final Integer port;
    private final RpcConfig config;

    private Channel channel;
    private Runnable sendFail = () -> {
    };

    public void client(Consumer<String> consumer) throws InterruptedException {
        Channel channel = NettyUtils.client(this.host, this.port,new ChannelInboundHandlerAdapter());
        this.channel = channel;
    }

    public ClientNetty(String host, Integer port) {
        this(host, port, new RpcConfig());
    }

    public ClientNetty(String host, Integer port, RpcConfig config) {
        this.host = host;
        this.port = port;
        this.config = config;
    }


    public R call(String path, T command, Class<R> resultClass) {
        Request request = Request.ofClient(path, command);
        channel.writeAndFlush(JsonUtil.toJson(request));
        return null;
    }

    public void connect() {
        // non impl
    }

    @Override
    public void onFailed() {
        sendFail.run();
    }

    public void setSendFail(Runnable sendFail) {
        this.sendFail = sendFail;
    }

    public void close() {
        // null;

        if (channel != null) {
            channel.close();
        }
    }
}
