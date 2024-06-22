package org.lee;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.jupiter.api.Test;
import org.lee.election.Endpoint;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.common.RpcUtil;
import org.lee.rpc.netty.ClientNetty;
import org.lee.rpc.netty.ServerNetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ByteBufErrorTest {

    int port = 80;

    @Test
    public void test() throws InterruptedException, IOException {
        CompletableFuture.runAsync(() -> {

            try {
                ServerNetty.start1(new ChannelInboundHandlerAdapter() {
                                       @Override
                                       public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                           String s = RpcUtil.readToString(((ByteBuf) msg));
                                           System.out.println("read str"+s);
                                       }
                                   },
                        port,
                        this::send);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100000);
    }

    void send() {
        RpcCaller<String, String> client = new ClientNetty<>("localhost", port);
        client.connect();
        String call = client.call(
                "path",
                "req",
                String.class
        );
        System.out.println(call);
    }
}
