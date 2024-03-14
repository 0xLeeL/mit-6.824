package org.lee.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {


    private final Map<String,Handler> handlerMap = new ConcurrentHashMap<>();
    public <T> Object dispatch(String path, String requestJson){
        Handler objectObjectHandler = handlerMap.get(path);
        return objectObjectHandler.handle(requestJson);
    }

    public void register(String path, Handler function) {
        handlerMap.put(path, function);
    }

}
