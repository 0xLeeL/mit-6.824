package org.lee.log;

import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;

import java.util.LinkedList;

public class SyncHandler implements Handler {
    private final LinkedList<LogEntry> entries = new LinkedList<>();
    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        entries.add(logEntry);
        return SyncResult.success();
    }

    public LinkedList<LogEntry> getEntries() {
        return entries;
    }
}
