//package org.lee.lab1;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.lee.common.SocketUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
///**
// * map reduce 测试案例
// */
//
//public class LAB1Test {
//    private final Logger log = LoggerFactory.getLogger(LAB1Test.class);
//
//    public static int masterPort = 80;
//    public static int worker1 = 81;
//    public static int worker2 = 82;
//
//    @Test
//    void test_number_sum_test() {
//        Master<Long, Long> master = new Master<>(masterPort);
//        List<Long> integers = IntStream.range(0, 100).mapToLong(d -> d).boxed().toList();
//        Long submit = master.submit(
//                integers,
//                (li) -> {
//                    int sum = 0;
//                    for (Long aLong : li) {
//                        sum += aLong;
//                    }
//                    try {
//                        Thread.sleep(1000, 100);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    return (long) sum;
//                },
//                (result) -> result.stream().mapToLong(c -> c).sum()
//        );
//        int sum = IntStream.range(0, 100).sum();
//        Assertions.assertEquals(sum, submit);
//    }
//
//    @Test
//    void test_single_() {
//        long sum = IntStream.range(0, 1 << 30).mapToLong(d -> d).sum();
//        System.out.println(sum);
//    }
//
//    @Test
//    void test_worker() throws Exception {
//        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker1));
//        Thread.sleep(1000);
//        try (Socket socket = new Socket("localhost", worker1)) {
//            OutputStream outputStream = socket.getOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//
//            List<Long> integers = IntStream.range(0, 100).mapToLong(d -> d).boxed().toList();
//            objectOutputStream.writeObject(new Task<>(
//                    integers,
//                    (li) -> {
//                        int sum = 0;
//                        for (Long aLong : li) {
//                            sum += aLong;
//                        }
//                        try {
//                            Thread.sleep(1000, 100);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        return (long) sum;
//                    }
//            ));
//            InputStream inputStream = socket.getInputStream();
//            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//            Object o = objectInputStream.readObject();
//            Assertions.assertEquals(4950L, o);
//        }
//    }
//
//    @Test
//    void test_worker_fail() throws Exception {
//        List<Long> collect = IntStream.range(0, 1 << 10).mapToLong(d -> d).boxed().collect(Collectors.toList());
//        Master<Long, Long> master = new Master<>(masterPort);
//        Thread.sleep(1000);
//        AtomicReference<Worker<Long, Long>> w1 = new AtomicReference<>();
//        AtomicReference<Worker<Long, Long>> w2 = new AtomicReference<>();
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//
//        CompletableFuture.runAsync(() -> {
//            w1.set(new Worker<>(masterPort, worker1));
//        }, executorService);
//        CompletableFuture.runAsync(() -> {
//            w2.set(new Worker<>(masterPort, worker2));
//        }, executorService);
//        Thread.sleep(2000);
//        w2.get().close();
//        Long submit = master.submit(
//                collect,
//                (li) -> {
//                    int sum = 0;
//                    for (Long aLong : li) {
//                        sum += aLong;
//                    }
//                    return (long) sum;
//                },
//                (result) -> {
//                    log.info("{}", result);
//                    return result.stream().mapToLong(d -> d).sum();
//                }
//        );
//        System.out.println(submit);
//        System.out.println(collect.stream().mapToLong(d -> d).sum());
//        Assertions.assertEquals(collect.stream().mapToLong(d -> d).sum(), submit);
//    }
//
//    @Test
//    void test_worker_all_fail() throws Exception {
//        List<Long> collect = IntStream.range(0, 1 << 10).mapToLong(d -> d).boxed().collect(Collectors.toList());
//        Master<Long, Long> master = new Master<>(masterPort);
//        Thread.sleep(1000);
//        AtomicReference<Worker<Long, Long>> w1 = new AtomicReference<>();
//        AtomicReference<Worker<Long, Long>> w2 = new AtomicReference<>();
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//
//        CompletableFuture.runAsync(() -> {
//            w1.set(new Worker<>(masterPort, worker1));
//        }, executorService);
//        CompletableFuture.runAsync(() -> {
//            w2.set(new Worker<>(masterPort, worker2));
//        }, executorService);
//        Thread.sleep(2000);
//        w2.get().close();
//        w1.get().close();
//        Long submit = master.submit(
//                collect,
//                (li) -> {
//                    int sum = 0;
//                    for (Long aLong : li) {
//                        sum += aLong;
//                    }
//                    return (long) sum;
//                },
//                (result) -> {
//                    log.info("{}", result);
//                    return result.stream().mapToLong(d -> d).sum();
//                }
//        );
//        System.out.println(submit);
//        System.out.println(collect.stream().mapToLong(d -> d).sum());
//        Assertions.assertEquals(collect.stream().mapToLong(d -> d).sum(), submit);
//    }
//
//    @Test
//    void test_worker_all() throws Exception {
//        List<Long> collect = IntStream.range(0, 1 << 10).mapToLong(d -> d).boxed().collect(Collectors.toList());
//        Master<Long, Long> master = new Master<>(masterPort);
//        Thread.sleep(1000);
//        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker1));
//        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker2));
//        Thread.sleep(1000);
//        Long submit = master.submit(
//                collect,
//                (li) -> {
//                    int sum = 0;
//                    for (Long aLong : li) {
//                        sum += aLong;
//                    }
//                    return (long) sum;
//                },
//                (result) -> {
//                    log.info("{}", result);
//                    return result.stream().mapToLong(d -> d).sum();
//                }
//        );
//        System.out.println(submit);
//        System.out.println(collect.stream().mapToLong(d -> d).sum());
//        Assertions.assertEquals(collect.stream().mapToLong(d -> d).sum(), submit);
//    }
//
//
////    @Test
////    void test_worker1_boot() throws InterruptedException {
////        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker1));
////        Thread.sleep(100000000);
////    }
////
////    @Test
////    void test_worker2_boot() throws InterruptedException {
////        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker2));
////        Thread.sleep(100000000);
////    }
//
//    @Test
//    void test_master_boot() {
//        List<Long> collect = IntStream.range(0, 1 << 10).mapToLong(d -> d).boxed().collect(Collectors.toList());
//        Master<Long, Long> master = new Master<>(masterPort);
//        Long submit = master.submit(
//                collect,
//                (li) -> {
//                    int sum = 0;
//                    for (Long aLong : li) {
//                        sum += aLong;
//                    }
//                    return (long) sum;
//                },
//                (result) -> {
//                    log.info("{}", result);
//                    return result.stream().mapToLong(d -> d).sum();
//                }
//        );
//        System.out.println(submit);
//        System.out.println(collect.stream().mapToLong(d -> d).sum());
//        Assertions.assertEquals(collect.stream().mapToLong(d -> d).sum(), submit);
//    }
//
//
//    @Test
//    public void test_rpc() throws Exception {
//        ServerSocket server0 = new ServerSocket(80);
//        CompletableFuture.runAsync(() -> {
//            try {
//                Socket socket;
//                while ((socket = server0.accept()) != null) {
//                    InputStream stream = socket.getInputStream();
//                    Object o = SocketUtil.readObject(stream);
//                    System.out.println(o);
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        });
////        ServerSocket server1 = new ServerSocket(81);
//        Socket socket = new Socket("localhost", 80);
//        SocketUtil.objectSend("a", socket.getOutputStream());
//        InputStream inputStream = socket.getInputStream();
//        Object o = SocketUtil.readObject(inputStream);
//        System.out.println(o);
//    }
//
//    @Test
//    void test_o() {
//        System.out.println("xxx");
//    }
//}
