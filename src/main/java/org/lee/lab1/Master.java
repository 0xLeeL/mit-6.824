package org.lee.lab1;


import lombok.extern.slf4j.Slf4j;
import org.lee.common.Pair;
import org.lee.common.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class Master<ARG, RESULT> {
    List<Integer> workers = new CopyOnWriteArrayList<>();
    List<RESULT> results = new CopyOnWriteArrayList<>();

    public Master(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            log.info("bind successfully");
            registerListener(serverSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void registerListener(ServerSocket serverSocket) {
        CompletableFuture.runAsync(() -> {
            try {
                Socket socket;
                log.info("prepare register");
                while ((socket = serverSocket.accept()) != null) {
                    InputStream stream = socket.getInputStream();
                    Object o = SocketUtil.readObject(stream);
                    tackle(o);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void tackle(Object o) {
        if (o instanceof Integer) {
            Integer integer = (Integer) o;
            workers.add(integer);
            log.info("worker {} register", integer);
        } else if (o instanceof MinResult<?>) {
            results.add(((RESULT) ((MinResult<?>) o).getMid()));
        }
    }

    public RESULT submit(List<ARG> arg,
                         Function1<List<ARG>, RESULT> map,
                         Function1<List<RESULT>, RESULT> reduce) {


        List<Pair<Integer, List<ARG>>> partition = partition(arg);
        List<RESULT> lists = new CopyOnWriteArrayList<>();
        CompletableFuture<RESULT>[] array = partition
                .stream()
                .map(c -> CompletableFuture.supplyAsync(() -> {
                                    // TODO 改成任务提交的方式给 worker 执行 fork 操作
                                    return dispatch(c.first, c.second, map);
                                })
                                .whenComplete(
                                        (result, throwable) -> {
                                            log.info("result: {}, throw: ", result, throwable);
                                            if (result != null) lists.add(result);
                                            if (throwable != null) log.error(throwable.getMessage(), throwable);
                                        }
                                )
                )
                .<CompletableFuture<RESULT>>toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(array).join();
        return reduce.apply(lists);
    }


    public List<Pair<Integer, List<ARG>>> partition(List<ARG> arg) {
        int index = arg.size() / workers.size();
        ArrayList<Pair<Integer, List<ARG>>> objects = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < arg.size() - 1; i += index, j++) {
            int end = i + index;
            if (i + index > arg.size()) {
                end = workers.size();
            }
            objects.add(Pair.of(
                    workers.get(j),
                    new ArrayList<>(arg.subList(i, end))
            ));
        }
        return objects;
    }


    RESULT dispatch(int port, List<ARG> arg,
                    Function1<List<ARG>, RESULT> map) {
        try (Socket socket = new Socket("localhost", port)) {
            SocketUtil.objectSend(new Task<>(arg, map), socket.getOutputStream());
            return (RESULT) SocketUtil.readObject(socket.getInputStream());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return null;
    }

}
