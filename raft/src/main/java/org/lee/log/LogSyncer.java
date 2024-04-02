package org.lee.log;

import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.common.utils.ThreadUtil;
import org.lee.common.utils.TimerUtils;
import org.lee.election.Endpoint;
import org.lee.log.domain.FailAnalyze;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.log.handler.LogSyncHandler;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadPoolExecutor;

public class LogSyncer {
    private final Logger log = LoggerFactory.getLogger(LogSyncer.class);
    private final Context context;
    private final ThreadPoolExecutor pool = ThreadUtil.poolOfIO("Log-Fail-Recovery");
    private final Map<Endpoint, FailAnalyze> syncFailedEndpoints = new ConcurrentHashMap<>();

    public LogSyncer(Context context) {
        this.context = context;
    }


    public void syncing() {
        TimerUtils.schedule(() -> {
            String entry = getEntry();
            sync(entry);
        }, 1000, 2000);
    }

    private String getEntry() {
        return "test entry";
    }

    public void sync(String logEntry) {
        int indexOfEpoch = context.incrementIndexOfEpoch();
        int epoch = context.getEpoch();
        context.getEndpoints()
                .parallelStream()
                .filter(endpoint -> !syncFailedEndpoints.containsKey(endpoint))
                .forEach(endpoint -> {
                    try {
                        SyncResult syncResult = endpoint.sendLog(new LogEntry(context.getEpoch(), logEntry, indexOfEpoch));
                        log.info("{} syncing result is :{}", endpoint.info(), syncResult);
                        if (syncResult.failed()) {
                            syncFailedEndpoints.put(endpoint, new FailAnalyze(endpoint, syncResult, epoch, indexOfEpoch));
                        }
                    } catch (Exception e) {
                        log.error("sync log failed:{},{}", endpoint, e.getMessage(), e);
                    }
                });
    }

    protected void recovery() {

    }

    protected void recoveryOne(FailAnalyze failAnalyze) {

    }

    public static LogSyncHandler follow(Server server) {
        LogSyncHandler handler = new LogSyncHandler(server);
        server.register(Constant.LOG_SYNC_PATH, handler);
        return handler;
    }
}
