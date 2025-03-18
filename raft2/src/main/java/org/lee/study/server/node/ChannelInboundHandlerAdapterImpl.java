package org.lee.study.server.node;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.lee.study.common.JsonUtil;
import org.lee.study.server.node.domain.Request;

@Slf4j
public class ChannelInboundHandlerAdapterImpl extends ChannelInboundHandlerAdapter {
    private final NodeDispatcher nodeDispatcher = new NodeDispatcher();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("received :{}", msg);
        if (msg instanceof String json) {

            Request request = JsonUtil.fromJson(json, Request.class);
            String path = request.getPath();
            log.info("path:{}", path);

            request.setResponse(response -> {
                log.info("to send:{}", response);
                String resp = response instanceof String ? (String) response : JsonUtil.toJson(response);
                Channel r = ctx.channel();
                r.writeAndFlush(resp);
            });
            nodeDispatcher.dispatch(request);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有链接进来");
        super.channelActive(ctx);
    }
}