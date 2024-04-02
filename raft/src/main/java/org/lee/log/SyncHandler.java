package org.lee.log;

import org.lee.common.Global;
import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Handler;

import java.util.LinkedList;

public class SyncHandler implements Handler {
    private final Global global;
    private final LinkedList<LogEntry> entries = new LinkedList<>();

    public SyncHandler(Global global) {
        this.global = global;
    }

    @Override
    public Object handle(String requestJson) {
        LogEntry logEntry = JsonUtil.fromJson(requestJson, LogEntry.class);
        if (canSync(logEntry)) {
            entries.add(logEntry);
            return SyncResult.success();
        }
        return SyncResult.fail(global.getEpoch(), global.getIndexOfEpoch());
    }

    private boolean canSync(LogEntry logEntry) {
        return logEntry.epoch() >= global.getEpoch() && logEntry.epochIndex() > global.getIndexOfEpoch();
    }

    public LinkedList<LogEntry> getEntries() {
        return entries;
    }
}
