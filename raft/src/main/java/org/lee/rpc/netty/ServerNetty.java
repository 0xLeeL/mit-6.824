package org.lee.rpc.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.JsonUtil;
import org.lee.rpc.Request;
import org.lee.rpc.Server;
import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * The class listen command from client(or other server),and response result
 */
public class ServerNetty extends Server {


    private static final Logger log = LoggerFactory.getLogger(ServerNetty.class);


    public ServerNetty(int listenPort) {
        super(listenPort);
    }

    public ServerNetty(GlobalConfig globalConfig, Context context) {
        super(globalConfig, context);
    }


    public CompletableFuture<Void> listen() {

        try {
            start1(getGlobalConfig().getCurrentPort());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.runAsync(() -> {
        });
    }

    public void start1(int port) throws InterruptedException {
        NettyUtils.server(port, new ChannelInboundHandlerAdapterImpl());
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ChannelHandler.Sharable
    public class ChannelInboundHandlerAdapterImpl extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("received :{}", msg);
            if (msg instanceof String json) {

                Request request = JsonUtil.fromJson(json, Request.class);
                String path = request.getPath();
                log.info("path:{}", path);

                request.setResponse(response -> {
                    log.info("tosend:{}", response);
                    String resp = response instanceof String ? (String) response : JsonUtil.toJson(response);
                    Channel r = ctx.channel();
                    r.writeAndFlush(resp);
                });
                getDispatcher().dispatch(request);
            }
        }
    }
}
