package org.lee.rpc.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.rpc.Request;
import org.lee.rpc.Response;
import org.lee.rpc.Server;
import org.lee.rpc.common.RpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * The class listen command from client(or other server),and response result
 */
public class ServerNetty extends Server {


    private final Logger log = LoggerFactory.getLogger(ServerNetty.class);


    public ServerNetty(int listenPort) {
        super(listenPort);
    }

    public ServerNetty(GlobalConfig globalConfig, Context context) {
        super(globalConfig, context);
    }


    public CompletableFuture<Void> listen() {
        return CompletableFuture.runAsync(() -> {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 2));
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("received :{}",msg);
                            if (msg instanceof ByteBuf bb){
                                String path = RpcUtil.readToString(bb);
                                String requestJson = RpcUtil.readToString(bb);
                                getDispatcher().dispatch(new Request(requestJson, path, response -> RpcUtil.send(ctx.channel(), response)));

                            }
                        }
                    });
                }
            });
            try {
                int port = getGlobalConfig().getCurrentPort();
                ChannelFuture closeFuture = bootstrap.bind(port)
                        .addListener((ChannelFutureListener) future1 -> {
                            if (future1.isSuccess()) {
                                log.info("started on :{}", port);
                            }else{
                                log.error("falied:{},{}",port, future1.cause().getMessage(),future1.cause());
                            }
                        })
                        .channel()
                        .closeFuture().sync();
            } catch (InterruptedException e) {
                log.warn("start failed");
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
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
}
