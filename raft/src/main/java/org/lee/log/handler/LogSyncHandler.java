package org.lee.log.handler;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;
import org.lee.rpc.Server;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.store.service.DBServiceInConcurrentHashMap;

import java.util.LinkedList;

@Slf4j
public class LogSyncHandler implements Handler {
    private final Context context;
    private final LinkedList<LogEntry> entries = new LinkedList<>();

    private final DBServiceUseCase dbService = DBServiceInConcurrentHashMap.getInstance();

    public LogSyncHandler(Server server) {
        this.context = server.getContext();
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        log.info("{} log entry:{}", context.getCurrentActor(), logEntry);
        if (!canSync(logEntry)) {
            return SyncResult.fail(context.getEpoch(), context.getIndexOfEpoch());
        }
        entries.add(logEntry);
        trySyncData(logEntry);
        return SyncResult.success();
    }

    private void trySyncData(LogEntry data) {
        if (!data.putData()){
            return;
        }
        PutRequest putRequest = JsonUtil.fromJson(data.data().toString(), PutRequest.class);
        dbService.set(putRequest.key(),putRequest.value());
    }

    private boolean canSync(LogEntry logEntry) {
        return logEntry.epoch() >= context.getEpoch() && logEntry.epochIndex() > context.getIndexOfEpoch();
    }

    public LinkedList<LogEntry> getEntries() {
        return entries;
    }
}
