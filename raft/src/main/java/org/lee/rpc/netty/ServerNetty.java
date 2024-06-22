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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture.runAsync(() -> {
            try {
                start1(new ChannelInboundHandlerAdapterImpl(), getGlobalConfig().getCurrentPort(), countDownLatch::countDown);
            } catch (InterruptedException e) {
                log.warn("start failed");
                log.error(e.getMessage(), e);
                countDownLatch.countDown();
                throw new RuntimeException(e);
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.runAsync(() -> {
        });
    }


    public static void start1(ChannelInboundHandler inboundHandler, int port) throws InterruptedException {
        start1(inboundHandler, port, () -> {
        });
    }

    public static void start1(ChannelInboundHandler inboundHandler, int port, Runnable success) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 2));
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast(
                                new LengthFieldBasedFrameDecoder(
                                        1024 * 1024,
                                        0, 2,
                                        0, 2))
                        .addLast(inboundHandler)
                        .addLast(new MessageToByteEncoder() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
                                log.info("out:{}", msg);
                                RpcUtil.write(out, msg);
                            }
                        });
            }
        });

        ChannelFuture closeFuture = bootstrap.bind(port)
                .addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        log.info("started on :{}", port);
                        success.run();
                        ;
                    } else {
                        log.error("falied:{},{}", port, future1.cause().getMessage(), future1.cause());
                    }
                })
                .channel()
                .closeFuture().sync();
    }

    @Override
    public void close() {
        try {
            System.out.println("close");
            throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class ChannelInboundHandlerAdapterImpl extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("received :{}", msg);
            if (msg instanceof ByteBuf bb) {
                String path = RpcUtil.readToString(bb);
                log.info("path:{}", path);
                String requestJson = RpcUtil.readToString(bb);
                getDispatcher().dispatch(new Request(requestJson, path, response -> {
                    log.info("tosend:{}", response);
                    Channel r = ctx.channel();
                    r.writeAndFlush(response);
                }));

            }
        }
    }
}
