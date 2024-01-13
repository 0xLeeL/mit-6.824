package org.lee.common.rpc;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.Map;


//
// 基于通道的RPC，用于6.5840实验室。
//
// 模拟一个可能丢失请求、丢失回复、延迟消息和完全断开特定主机连接的网络。
//
// 我们将使用原始的labrpc.go文件来测试您的代码，以便评分。
// 因此，尽管您可以修改此代码以帮助您调试，但请在提交前针对原始代码进行测试。
//
// 改编自Go net/rpc/server.go。
//
// 发送labgob编码的值以确保RPC
// 不包含对程序对象的引用。
//
// net := MakeNetwork() -- 创建网络、客户端、服务器。
// end := net.MakeEnd(endname) -- 创建客户端端点，与一个服务器通信。
// net.AddServer(servername, server) -- 向网络添加一个命名的服务器。
// net.DeleteServer(servername) -- 删除一个命名的服务器。
// net.Connect(endname, servername) -- 将客户端连接到服务器。
// net.Enable(endname, enabled) -- 启用/禁用一个客户端。
// net.Reliable(bool) -- 设置为false表示丢弃/延迟消息
//
// end.Call("Raft.AppendEntries", &args, &reply) -- 发送RPC，等待回复。
// "Raft"是被调用的服务器结构的名称。
// "AppendEntries"是被调用的方法的名称。
// Call()返回true表示服务器执行了请求
// 并且回复是有效的。
// 如果网络丢失了请求或回复
// 或者服务器宕机，Call()返回false。
// 同一ClientEnd上可以同时进行多个Call()。
// 并发的Call()可能会被服务器无序接收，
// 因为网络可能会重新排序消息。
// Call()保证返回（可能会有延迟），*除非*服务器端的
// 处理函数没有返回。
// 服务器端RPC处理函数必须将其args和reply参数
// 声明为指针，以便它们的类型与Call()的参数类型完全匹配。
//
// srv := MakeServer()
// srv.AddService(svc) -- 一个服务器可以有多个服务，例如Raft和k/v
//   将srv传递给net.AddServer()
//
// svc := MakeService(receiverObject) -- obj的方法将处理RPCs
//   类似于Go的rpcs.Register()
//   将svc传递给srv.AddService()
//

/**
 * 需要完成的功能，
 * 1. 丢失请求
 * 2. 丢失回复
 * 3. 延迟消息
 * 4. 完全断开
 * 5. 禁用/启用客户端
 */
public class Rpc {
    public static int serverPort = 80;


    // RPC请求消息类
    static class ReqMsg {
        public String svcMeth; // 例如 "Raft.AppendEntries"
        // ... 其他字段和方法
    }

    // RPC响应消息类
    static class ReplyMsg {
        public boolean ok;
        public Object reply;
        // ... 其他字段和方法
    }

    // 客户端端点
    @Slf4j
    static class ClientEnd {
        DelayQueue<DeplaydEle> delayQueue = new DelayQueue<>();

        DeplaydEle take; // 超时记录
        private long timeoutMill = 5_000;
        private String endName;
        private Thread processThread;
        Socket socket;
        // 这里可以添加网络连接和其他逻辑

        public ClientEnd(String endName) {
            this.endName = endName;
        }

        /**
         * 模拟一个可能丢失请求、丢失回复、延迟消息和完全断开特定主机连接的网络。
         * 1. 请求丢失
         * 2. 丢失回复
         * 3. 延迟消息
         * 4. 完全断开连接
         *
         * @param svcMeth
         * @param args
         * @param reply
         * @return
         */
        public boolean call(String svcMeth, Object args, Object reply) {
            // 实现RPC调用逻辑
            try {
                socket = new Socket("localhost", serverPort);
                SocketUtil.objectSend((Serializable) args, socket.getOutputStream());
                log.info("客户端开始接收消息");
                processIlle();
                processThread = new Thread(() -> {
                }, "server-process");
                processThread.start();
            } catch (IOException e) {
                connectFailProcess();
                throw new RuntimeException(e);
            } finally {
                close();
            }
            return true; // 或者根据实际情况返回false
        }

        public void processIlle() {
            try {
                InputStream inputStream = socket.getInputStream();
                sendTimeoutProcess();
                // 解决block 之后的问题
                int read = inputStream.read();
                cancelTimeoutRecord();
                while (true) {
                    log.info("read:{}", (char) read);
                    read = inputStream.read();
                    if (read == -1) {
                        log.info("处理完成");
                        return;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                close();
            }
        }

        public void connectFailProcess() {
            log.info("连接失败处理");
        }

        public void sendTimeoutProcess() {
            take = new DeplaydEle("", timeoutMill);
            delayQueue.put(take);// 注册超时事件
            CompletableFuture.runAsync(() -> {
                try {
                    delayQueue.take();
                    timeoutProcess();
                    processThread.interrupt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void cancelTimeoutRecord() {
            delayQueue.remove(take);
        }

        /**
         * 超时之后需要处理的处理方法，各种资源的close
         */
        public void timeoutProcess() {
            log.info("超时处理");
            close();
        }

        public void close() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 服务器
    @Slf4j
    static class Server {
        // 服务器逻辑

        ServerSocket serverSocket;
        Runnable process;

        public Server(Runnable process) {
            try {
                this.process = process;
                this.serverSocket = new ServerSocket(serverPort);
                listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void listen() {
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("rpc服务端启动成功");
                    while (true) {
                        Socket accept = serverSocket.accept();
                        process(accept);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        int i = 0;

        void process(Socket accept) {
            new Thread(() -> {
                try {
                    process.run();
                    InputStream inputStream = accept.getInputStream();
                    Object o = SocketUtil.readObject(inputStream);
                    log.info("服务端收到：{}", o);
                    // resp
                    OutputStream outputStream = accept.getOutputStream();
                    outputStream.write("demo".getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    outputStream.close();
//                    SocketUtil.objectSend("success", outputStream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "server-thread-" + i++).start();
        }

        public void close(){
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 网络
    static class Network {
        private Map<String, ClientEnd> clientEnds;
        private Map<String, Server> servers;
        private boolean reliable;

        public Network() {
            clientEnds = new ConcurrentHashMap<>();
            servers = new ConcurrentHashMap<>();
            reliable = true;
        }

        public void addClientEnd(String endName, ClientEnd clientEnd) {
            clientEnds.put(endName, clientEnd);
        }

        public void addServer(String serverName, Server server) {
            servers.put(serverName, server);
        }

        public void setReliable(boolean reliable) {
            this.reliable = reliable;
        }

        // 其他网络操作方法
    }

    // 示例使用

}
