package org.lee.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.jupiter.api.Test;
import org.lee.rpc.netty.NettyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lee.rpc.netty.NettyUtils.client;

public class NettyTest {
    private final Logger log = LoggerFactory.getLogger(NettyTest.class);

    @Test
    void test_() throws InterruptedException {
        int port = 80;
        NettyUtils nettyUtils = new NettyUtils();
        nettyUtils.server(port);
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

}
