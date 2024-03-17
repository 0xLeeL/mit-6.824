//package org.lee.common.rpc;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class RpcTest {
//
//
//    @Test
//    void test_normal() throws InterruptedException {
//        Rpc.Network network = new Rpc.Network();
//        Rpc.ClientEnd clientEnd = new Rpc.ClientEnd("client1");
//        Rpc.Server server = new Rpc.Server(()->{});
//
//        network.addClientEnd("client1", clientEnd);
//        network.addServer("server1", server);
//
//        // 进行RPC调用
//        clientEnd.call("mesh", "发送的消息", new Object());
//        server.close();
////        Thread.sleep(10000);
//    }
//    @Test
//    void test_normal_timeout(){
//        Rpc.Network network = new Rpc.Network();
//        Rpc.ClientEnd clientEnd = new Rpc.ClientEnd("client1");
//        Rpc.Server server = new Rpc.Server(()->{
//            try {
//                Thread.sleep(30_000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        network.addClientEnd("client1", clientEnd);
//        network.addServer("server1", server);
//
//        // 进行RPC调用
//        Assertions.assertThrows(Exception.class,()->{
//
//            // 进行RPC调用
//            clientEnd.call("mesh", "发送的消息", new Object());
//        });
////        clientEnd.call("mesh", "发送的消息", new Object());
////        Thread.sleep(10000);
//    }
//    @Test
//    void test_normal_connect_fail() {
//        Rpc.Network network = new Rpc.Network();
//        Rpc.ClientEnd clientEnd = new Rpc.ClientEnd("client1");
//
//        network.addClientEnd("client1", clientEnd);
//
//        Assertions.assertThrows(Exception.class,()->{
//
//            // 进行RPC调用
//            clientEnd.call("mesh", "发送的消息", new Object());
//        });
//    }
//}
