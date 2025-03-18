package org.lee.study.server;

import io.netty.channel.*;
import org.junit.jupiter.api.Test;
import org.lee.study.common.Constant;
import org.lee.study.common.JsonUtil;
import org.lee.study.server.node.Server;
import org.lee.study.server.node.domain.Request;
import org.lee.study.server.node.log.LogEntry;

import java.util.concurrent.CountDownLatch;

public class TestServer {

    @Test
    void test() throws InterruptedException {


        Server server = NettyUtils.raftNode(8080);
        while (!server.getChannel().isActive()){
            Thread.sleep(1);
        }
        sendMessage(Request.ofClient("hello","message"));
        sendMessage(Request.ofClient(Constant.APPEND_LOG, LogEntry.ofPutData(1,1,"hello")));
    }

    private static void sendMessage(Request request) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(1);
        Channel clientChannel = NettyUtils.client("localhost", 8080, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("client received: " + msg);
                count.countDown();
                super.channelRead(ctx, msg);
            }
        });

        clientChannel.writeAndFlush(JsonUtil.toJson(request));
        count.await();
        clientChannel.close();
    }
}
