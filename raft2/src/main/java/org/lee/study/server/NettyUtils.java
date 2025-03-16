package org.lee.study.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.lee.study.server.http.adapter.HttpInboundAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyUtils {
    private static final Logger log = LoggerFactory.getLogger(NettyUtils.class);


    public static void http(int port) throws InterruptedException {
        server(port, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // 添加 HTTP 编解码器
                pipeline.addLast(new HttpServerCodec());
                // 添加 HTTP 对象聚合器，处理完整的 HTTP 请求
                pipeline.addLast(new HttpObjectAggregator(65536));
                // 添加自定义处理器
                pipeline.addLast(new HttpInboundAdapter());
            }
        });
    }

    public static void server(int port, ChannelInitializer<SocketChannel> initializer) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 2));
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(initializer);
         bootstrap.bind(port)
                .addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        log.info("started on :{}", port);
                    } else {
                        log.error("falied:{},{}", port, future1.cause().getMessage(), future1.cause());
                    }
                }).channel().closeFuture().sync();
    }

    public static void handle(String req, Channel channel) {
        log.info("req:{}", req);
        channel.writeAndFlush("received");
    }

    public static void addLast(SocketChannel ch) {

        LengthFieldBasedFrameDecoder fieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 2, 0, 2);

        LengthFieldPrepender lengthFieldPrepender = new LengthFieldPrepender(4, 0, false);
        ch.pipeline()
                // in
                .addLast(new StringDecoder())
                .addLast(fieldBasedFrameDecoder)

                // out
                .addLast(new StringEncoder())
                .addLast(lengthFieldPrepender)
        ;
    }


    public static Channel client(String host, int port, ChannelInboundHandlerAdapter inboundHandlerAdapter) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();

        return bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        NettyUtils.addLast(ch);
                        ch.pipeline().addLast(inboundHandlerAdapter);
//                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
//                                    @Override
//                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//                                        System.out.println("client write 1" + msg);
//                                        super.write(ctx, msg, promise);
//                                    }
//                                })
//                                .addLast(new ChannelOutboundHandlerAdapter() {
//                                    @Override
//                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//                                        System.out.println("client write 2" + msg);
//                                        super.write(ctx, msg, promise);
//                                    }
//                                });

//                        ch.pipeline()
//                                .addLast(new ChannelInboundHandlerAdapter(){
//                                    @Override
//                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                        System.out.println("1 client received: "+msg);
//                                        super.channelRead(ctx, msg);
//                                    }
//                                })
//                                .addLast(new ChannelInboundHandlerAdapter(){
//                                    @Override
//                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                        System.out.println("2 client received: "+msg);
//                                        super.channelRead(ctx, msg);
//                                    }
//                                })
//                        ;
//                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
//                            @Override
//                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//                                System.out.println("tail:" + msg);
//                                super.write(ctx, msg, promise);
//                            }
//                        });
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                            @Override
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                System.out.println("read: " + msg);
//                                super.channelRead(ctx, msg);
//                            }
//                        });
                    }
                })
                .connect(new InetSocketAddress(host, port))
                .sync()
                .channel();
    }
}
