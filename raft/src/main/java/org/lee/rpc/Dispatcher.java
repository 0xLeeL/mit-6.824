package org.lee.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {


    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
    private final Map<String,Handler> handlerMap = new ConcurrentHashMap<>();
    public <T> Object dispatch(String path, String requestJson){
        Handler objectObjectHandler = handlerMap.get(path);
        if (Objects.isNull(objectObjectHandler)){
            log.error("path:{} 404",path);
            return "";
        }
        return objectObjectHandler.handle(requestJson);
    }

    public void register(String path, Handler function) {
        handlerMap.put(path, function);
    }

}
