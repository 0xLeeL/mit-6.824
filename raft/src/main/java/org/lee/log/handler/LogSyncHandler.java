package org.lee.log.handler;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Context;
import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.log.service.LogWriter;
import org.lee.rpc.Handler;
import org.lee.rpc.Server;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.store.service.DBServiceInConcurrentHashMap;

import java.util.List;

@Slf4j
public class LogSyncHandler implements Handler {
    private final Context context;
    private LogWriter logWriter;

    private final DBServiceUseCase dbService = DBServiceInConcurrentHashMap.getInstance();

    public LogSyncHandler(Server server) {
        this.context = server.getContext();
        this.logWriter = context.getLogWriter();
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        log.info("{} log entry:{}", context.getCurrentActor(), logEntry);
        if (!canSync(logEntry)) {
            return SyncResult.fail(context.getEpoch(), context.getIndexOfEpoch());
        }
        addLogEntry(logEntry);
        trySyncData(logEntry);
        return SyncResult.success();
    }

    private void addLogEntry(LogEntry logEntry) {
        logWriter.write(logEntry);
    }

    private void trySyncData(LogEntry data) {
        if (!data.putData()){
            return;
        }
        if (data.data() instanceof String strJson) {
            PutRequest putRequest = JsonUtil.fromJson(strJson, PutRequest.class);
            dbService.set(putRequest.key(),putRequest.value());
        }
    }

    private boolean canSync(LogEntry logEntry) {
        return logEntry.epoch() >= context.getEpoch() && logEntry.epochIndex() > context.getIndexOfEpoch();
    }

    public List<LogEntry> getEntries() {
        return logWriter.read();
    }

    public static void main(String[] args) {
        String v = "{\"key\":\"aaa\",\"value\":\"bbb\"}";
        System.out.println(JsonUtil.fromJson(JsonUtil.toJson(v), PutRequest.class));
    }
}
