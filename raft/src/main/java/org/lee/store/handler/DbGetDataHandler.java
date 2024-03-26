package org.lee.store.handler;

import org.lee.common.utils.JsonUtil;
import org.lee.rpc.Handler;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.GetRequest;
import org.lee.store.service.DBServiceInConcurrentHashMap;

public class DbGetDataHandler implements Handler {
    private final DBServiceUseCase dbService = DBServiceInConcurrentHashMap.getInstance();
    @Override
    public Object handle(String requestJson) {
        GetRequest getRequest = JsonUtil.fromJson(requestJson, GetRequest.class);
        String key = getRequest.key();
        return dbService.get(key);
    }
}
