package org.lee.study.server.http.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Dispatcher {
    private final static String  DELIMITER = "/";
    private static final Map<String, Function<String,String>> handlerMapping = new ConcurrentHashMap<>();
    private final HttpController controller = new HttpController();

    public Dispatcher() {
        register();
    }

    public String dispatch(String uri){
        String prefix = parseUri(uri);
        Function<String,String> controller = handlerMapping.get(prefix);
        if (controller == null) {
            return "controller don not exist";
        }
        return controller.apply(uri);
    }
    public String parseUri(String uri){
        return uri.split(DELIMITER)[1];
    }
    /**
     * 暂时将url路径注册逻辑写死，方便处理
     */
    public void register(){
        handlerMapping.put("get", o->{
            String[] split = o.split(DELIMITER);
            return controller.get(split[2]);
        });
        handlerMapping.put("set", o -> {
            String[] split = o.split(DELIMITER);
            return String.valueOf(controller.set(split[2], split[3]));
        });
    }
}
