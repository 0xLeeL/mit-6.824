package org.lee.rpc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lee.common.utils.JsonUtil;
import org.lee.rpc.Request;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClientNetty<T, R> implements RpcCaller<T, R> {
    private static final Logger log = LoggerFactory.getLogger(ClientNetty.class);

    private final String host;
    private final Integer port;
    private final RpcConfig config;
    private String result;

    private Channel channel;
    private Runnable sendFail = () -> {
    };
    private CountDownLatch latch = new CountDownLatch(1);

    public void client(Consumer<String> responseHandler) throws InterruptedException {
        Channel channel = NettyUtils.client(this.host, this.port, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof String s) {
                    responseHandler.accept(s);
                }
                super.channelRead(ctx, msg);
            }
        });
        this.channel = channel;
    }

    public ClientNetty(String host, Integer port) {
        this(host, port, new RpcConfig());
        try {
            Consumer<String> responseHandler = s -> {
                result = s;
                latch.countDown();
            };
            client(responseHandler);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public ClientNetty(String host, Integer port, RpcConfig config) {
        this.host = host;
        this.port = port;
        this.config = config;
    }


    public R call(String path, T command, Class<R> resultClass) {
        Request request = Request.ofClient(path, command);
        channel.writeAndFlush(JsonUtil.toJson(request));
        try {
            boolean await = latch.await(10, TimeUnit.SECONDS);
            if (await) {
                if (String.class.equals(resultClass)) {
                    return (R) result;
                }
                return JsonUtil.fromJson(result, resultClass);
            }
            // await is false , read timeout.
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
