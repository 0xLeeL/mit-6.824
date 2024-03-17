package org.lee.log;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogSyncer {
    private final Logger log = LoggerFactory.getLogger(LogSyncer.class);
    private final Global global;

    public LogSyncer(Global global) {
        this.global = global;
    }

    public void sync(String logEntry) {
        int indexOfEpoch = global.incrementIndexOfEpoch();
        List<SyncResult> syncResults = global.getEndpoints()
                .parallelStream()
                .map(endpoint -> {
                    SyncResult syncResult = endpoint.sendLog(new LogEntry(global.getEpoch(), logEntry, indexOfEpoch));
                    log.info("{} syncing result is :{}", endpoint.info(), syncResult);
                    return syncResult;
                })
                .toList();
    }

    public static void start(Server server) {
        server.register(Constant.LOG_SYNC_PATH, new SyncHandler());
    }
}
