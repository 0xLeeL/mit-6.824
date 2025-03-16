package org.lee.tpc.handler;

import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.rpc.Handler;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;

public class WorkerPrepareHandler implements Handler {
    private final DBServiceUseCase dbServiceUseCase;

    public WorkerPrepareHandler(DBServiceUseCase dbServiceUseCase) {
        this.dbServiceUseCase = dbServiceUseCase;
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        PutRequest putRequest = logEntry.tryConvert();
        if (putRequest != null) {
            return dbServiceUseCase.prepare(putRequest.key(), putRequest.value());
        }
        return false;
    }
}

