package org.lee.log;

import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.Endpoint;
import org.lee.log.domain.FailAnalyze;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.log.handler.LogSyncHandler;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class LogSyncer {
    private final Logger log = LoggerFactory.getLogger(LogSyncer.class);
    private final Context context;
    private final ThreadPoolExecutor pool = ThreadUtil.poolOfIO("Log-Fail-Recovery");
    private final ThreadPoolExecutor heartbeat = ThreadUtil.poolOfIO("Log-Syncer");
    private final Map<Endpoint, FailAnalyze> syncFailedEndpoints = new ConcurrentHashMap<>();

    public LogSyncer(Context context) {
        this.context = context;
    }

    private String getEntry() {
        return "test entry";
    }

    /**
     * 二阶段提交实现，
     * 多数存储成功之后才为成功
     * @param logEntry
     */
    public void sync(String logEntry) {
        int indexOfEpoch = context.incrementIndexOfEpoch();
        int epoch = context.getEpoch();
        context.getEndpoints()
                .stream()
                .filter(endpoint -> !syncFailedEndpoints.containsKey(endpoint))
                .forEach(endpoint -> CompletableFuture.runAsync(() -> {
                    try {
                        SyncResult syncResult = endpoint.sendLog(new LogEntry(context.getEpoch(), logEntry, indexOfEpoch));
                        log.info("{} syncing result is :{}", endpoint.info(), syncResult);
                        if (syncResult.failed()) {
                            syncFailedEndpoints.put(endpoint, new FailAnalyze(endpoint, syncResult, epoch, indexOfEpoch));
                        }
                    } catch (Exception e) {
                        log.error("sync log failed:{},{}", endpoint, e.getMessage(), e);
                    }
                }, heartbeat));
    }

    /**
     * TODO: realize
     */
    protected void recovery() {

    }

    /**
     * TODO: realize
     */
    protected void recoveryOne(FailAnalyze failAnalyze) {

    }

    public static LogSyncHandler follow(Server server) {
        LogSyncHandler handler = new LogSyncHandler(server);
        server.register(Constant.LOG_SYNC_PATH, handler);
        return handler;
    }

}
