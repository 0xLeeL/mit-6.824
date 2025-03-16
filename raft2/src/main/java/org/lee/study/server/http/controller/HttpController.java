package org.lee.study.server.http.controller;

import org.lee.study.db.DBServiceInConcurrentHashMap;
import org.lee.study.db.DBServiceUseCase;

public class HttpController {
    private final DBServiceUseCase useCase = new DBServiceInConcurrentHashMap();


    public String get(String key) {
        Object o = useCase.get(key);
        return o == null ? "null" : o.toString();
    }

    public Boolean set(String key, String value) {
        return useCase.set(key, value);
    }
}
