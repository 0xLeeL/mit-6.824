package org.lee.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Dispatcher {


    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
    private final Map<String, Worker> handlerMap = new ConcurrentHashMap<>();

    public void dispatch(Request request) {
        String requestJson = request.getRequest();
        String path = request.getPath();
        Worker objectObjectHandler = handlerMap.get(path);
        log.info("path:[{}] data:[{}]",path,requestJson);
        if (Objects.isNull(objectObjectHandler)) {
            log.error("path:{} 404", path);
            request.getResponse().back("");
            return;
        }
        handle(objectObjectHandler,request);
    }

    public void register(String path, Handler function) {
        if (handlerMap.containsKey(path)) {
            log.warn("path:{} handler exist; please add function if wanana force update", path);
        }
        Worker worker = new Worker(function);
        handlerMap.put(path, worker);
        worker.start();
    }

    public static class Worker extends Thread{
        public LinkedBlockingQueue<Request> queue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
        private final Handler handler;

        public Worker(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (true){
                try {
                    Request take  = queue.take();
                    Object handle = handler.handle(take.getRequest());
                    take.getResponse().back(handle);
                } catch (Throwable e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

        public void handle(Request requestJson) {
            queue.offer(requestJson);
        }

    }

    public void handle(Worker handler,Request request){
        try {
            handler.handle(request);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            request.getResponse().back("");
        }
    }

}
