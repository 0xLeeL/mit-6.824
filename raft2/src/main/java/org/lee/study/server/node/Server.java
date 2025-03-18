package org.lee.study.server.node;

import io.netty.channel.Channel;

import java.io.IOException;

public class Server {
    private final Channel channel;

    public Server(Channel channel) {
        this.channel = channel;
    }

    public void close() throws IOException, InterruptedException {
        channel.close().sync();
    }

    public Channel getChannel() {
        return channel;
    }
}
