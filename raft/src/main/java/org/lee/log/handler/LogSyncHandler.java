package org.lee.log.handler;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Context;
import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;
import org.lee.rpc.Server;

import java.util.LinkedList;

@Slf4j
public class LogSyncHandler implements Handler {
    private final Context context;
    private final LinkedList<LogEntry> entries = new LinkedList<>();

    public LogSyncHandler(Server server) {
        this.context = server.getContext();
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        log.info("{} log entry:{}", context.getCurrentActor(), logEntry);
        if (canSync(logEntry)) {
            entries.add(logEntry);
            return SyncResult.success();
        }
        return SyncResult.fail(context.getEpoch(), context.getIndexOfEpoch());
    }

    private boolean canSync(LogEntry logEntry) {
        return logEntry.epoch() >= context.getEpoch() && logEntry.epochIndex() > context.getIndexOfEpoch();
    }

    public LinkedList<LogEntry> getEntries() {
        return entries;
    }
}
