package org.lee.study.server.node.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import org.lee.study.common.JsonUtil;
import org.lee.study.server.NettyUtils;

@Data
public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ChannelFuture send(Object object) {
        try {
            return NettyUtils.client(host, port, new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    super.channelRead(ctx, msg);
                }
            }).writeAndFlush(JsonUtil.toJson(object));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
