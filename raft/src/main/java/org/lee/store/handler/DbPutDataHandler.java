package org.lee.store.handler;

import org.lee.common.utils.JsonUtil;
import org.lee.rpc.Handler;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.store.service.DBServiceInConcurrentHashMap;

public class DbPutDataHandler implements Handler {
    private final DBServiceUseCase dbService = DBServiceInConcurrentHashMap.getInstance();

    @Override
    public Object handle(String requestJson) {
        PutRequest getRequest = JsonUtil.fromJson(requestJson, PutRequest.class);
        return dbService.set(getRequest.key(), getRequest.value());
    }
}
