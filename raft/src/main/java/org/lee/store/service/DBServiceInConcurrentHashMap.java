package org.lee.store.service;

import org.lee.store.core.DBServiceUseCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBServiceInConcurrentHashMap implements DBServiceUseCase {
    private static final DBServiceUseCase dbServiceUseCase = new DBServiceInConcurrentHashMap();
    private final Map<String,Object> DB = new ConcurrentHashMap<>();
    @Override
    public boolean set(String key, Object value) {
        DB.put(key,value);
        return true;
    }

    @Override
    public Object get(String key) {
        return DB.get(key);
    }

    public static DBServiceUseCase getInstance() {
        return dbServiceUseCase;
    }
}
