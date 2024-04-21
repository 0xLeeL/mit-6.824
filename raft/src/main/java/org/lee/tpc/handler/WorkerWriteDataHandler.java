package org.lee.tpc.handler;

import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.rpc.Handler;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.tpc.Worker;

public class WorkerWriteDataHandler implements Handler {
    private final DBServiceUseCase dbServiceUseCase;

    public WorkerWriteDataHandler(DBServiceUseCase dbServiceUseCase) {
        this.dbServiceUseCase = dbServiceUseCase;
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        PutRequest putRequest = logEntry.tryConvert();
        if (putRequest != null) {
            return dbServiceUseCase.prepare(putRequest.key(),putRequest.value());
        }
        return false;
    }
}

