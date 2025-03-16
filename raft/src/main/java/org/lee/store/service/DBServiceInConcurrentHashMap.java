package org.lee.store.service;

import org.lee.store.core.DBServiceUseCase;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DBServiceInConcurrentHashMap implements DBServiceUseCase {
    private static final DBServiceUseCase dbServiceUseCase = new DBServiceInConcurrentHashMap();
    private final Map<String,Object> DB = new ConcurrentHashMap<>();
    private final Set<String> PREPARE = new ConcurrentSkipListSet<>();
    @Override
    public boolean set(String key, Object value) {
        if (PREPARE.contains(key)) {
            return DB.put(key,value) == value;
        }
        return false;
    }

    @Override
    public Object get(String key) {
        return DB.get(key);
    }

    public static DBServiceUseCase getInstance() {
        return dbServiceUseCase;
    }

    @Override
    public boolean prepare(String key, Object value) {
        return PREPARE.add(key);
    }

    @Override
    public boolean rollback(String key, Object value) {
        boolean remove = PREPARE.remove(key);
        // remove data ?
        // do not remove temporally
        return remove;
    }
}
