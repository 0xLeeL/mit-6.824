//package org.lee.rpc;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import org.lee.common.JsonUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Function;
//
//import static org.lee.rpc.Constant.HTTP_CONTENT_LENGTH;
//
///**
// * The class listen command from client(or other server),and response result
// */
//public class ServerHttpImpl {
//    private final Logger log = LoggerFactory.getLogger(ServerHttpImpl.class);
//
//
//    private final int port;
//    private HttpServer server;
//
//    public ServerHttpImpl(int serverPort) {
//        this.port = serverPort;
//        listen();
//
//    }
//
//    public CompletableFuture<Void> listen() {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                log.info("server starting... ");
//                // Create a ServerHttpImpl instance on port 8000
//                server = HttpServer.create(new InetSocketAddress(8000), 0);
//
//                // Define the context, it means our server will treat requests which are sent to the "/test" path
//                server.createContext("/test", new MyHandler());
//
//                // Starts the server
//                server.start();
//                System.out.println("Server is listening on port 8000");
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    public <T, R> void register(String path, Function<T, R> function, Class<T> requestCls) {
//        server.createContext(path, exchange -> {
//            // parse the request
//            InputStream requestBody = exchange.getRequestBody();
//            byte[] bytes = requestBody.readAllBytes();
//            int length = Integer.parseInt(exchange.getRequestHeaders().get(HTTP_CONTENT_LENGTH).get(0));
//            String json = new String(bytes, 0, length);
//            T t = JsonUtil.fromJson(json, requestCls);
//
//            // deal the apply
//            R apply = function.apply(t);
//            String response = JsonUtil.toJson(apply);
//            byte[] bytes1 = response.getBytes();
//            int length1 = bytes1.length;
//            exchange.getResponseHeaders().set(HTTP_CONTENT_LENGTH, length1);
//            exchange.getResponseBody().write(bytes1);
//
//        });
//
//    }
//
//
//}
