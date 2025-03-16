package org.lee.study.server.http.adapter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.lee.study.server.http.controller.Dispatcher;

@Slf4j
public class HttpInboundAdapter extends SimpleChannelInboundHandler<FullHttpRequest> {
    Dispatcher dispatcher = new Dispatcher();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {

        log.info("server received: {}", request);
        FullHttpResponse response = notFindResponse();
        try {

            response = handle(request);
        } catch (Throwable t) {
            response = errorResponse();
        } finally {

            // 处理 Keep-Alive
            if (HttpUtil.isKeepAlive(request)) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // 发送响应
            ctx.writeAndFlush(response);
        }
    }

    private FullHttpResponse handle(FullHttpRequest request) {
        // 创建响应内容
//        String responseContent = "Hello from Netty HTTP Server!\n" +
//                "URI: " + request.uri() + "\n" +
//                "Method: " + request.method() + "\n";
        String responseContent = "request success \n result:" + dispatcher.dispatch(request.uri());
        // 创建 HTTP 响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8)
        );

        // 设置响应头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    public FullHttpResponse errorResponse() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
    public FullHttpResponse notFindResponse() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
}
