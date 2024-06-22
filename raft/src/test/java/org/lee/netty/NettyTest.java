package org.lee.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.jupiter.api.Test;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.common.RpcUtil;
import org.lee.rpc.netty.ClientNetty;
import org.lee.rpc.netty.NettyUtils;
import org.lee.rpc.netty.ServerNetty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.lee.rpc.netty.NettyUtils.client;

public class NettyTest {
    private final Logger log = LoggerFactory.getLogger(NettyTest.class);

    @Test
    void test_with_primitive() throws InterruptedException {
        int port = 80;
        NettyUtils.server(port);
        Channel clientChannel = client("localhost", port, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("client read message :" + msg);
                super.channelRead(ctx, msg);
            }
        });
        clientChannel.writeAndFlush("client send message");
        Thread.sleep(1000000);
    }

    @Test
    void test_netty_rpc_time() throws InterruptedException {
        int port = 80;
        NettyUtils.server(port);
        send(port);
        Thread.sleep(100000);
    }

    @Test
    void test_client_send() throws InterruptedException {
        int port = 81;
        send(port);
        Thread.sleep(100000);
    }

    void send(int port) {
        RpcCaller<String, String> client = new ClientNetty<>("localhost", port);
        client.connect();
        String call = client.call(
                "path",
                "req",
                String.class
        );
        log.info("client received : {}",call);
    }

}
