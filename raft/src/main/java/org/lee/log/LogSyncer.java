package org.lee.log;

import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.common.utils.ThreadUtil;
import org.lee.common.utils.TimerUtils;
import org.lee.log.domain.FailAnalyze;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadPoolExecutor;

public class LogSyncer {
    private final Logger log = LoggerFactory.getLogger(LogSyncer.class);
    private final Global global;
    private final ThreadPoolExecutor pool = ThreadUtil.poolOfIO("Log-Fail-Recovery");
    private final Set<FailAnalyze> syncFailedEndpoints = new ConcurrentSkipListSet<>();

    public LogSyncer(Global global) {
        this.global = global;
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
        int indexOfEpoch = global.incrementIndexOfEpoch();
        int epoch = global.getEpoch();
        global.getEndpoints()
                .stream()
                .filter(endpoint -> !syncFailedEndpoints.contains(endpoint))
                .forEach(endpoint -> {
                    try {
                        SyncResult syncResult = endpoint.sendLog(new LogEntry(global.getEpoch(), logEntry, indexOfEpoch));
                        log.info("{} syncing result is :{}", endpoint.info(), syncResult);
                        if (!syncResult.syncSucceed()) {
                            syncFailedEndpoints.add(new FailAnalyze(endpoint, syncResult, epoch, indexOfEpoch));
                        }
                    } catch (Exception e) {
                        log.error("sync log failed:{}", endpoint);
                    }
                });
    }

    protected void recovery() {

    }

    protected void recoveryOne(FailAnalyze failAnalyze) {

    }

    public static SyncHandler follow(Server server) {
        SyncHandler handler = new SyncHandler(new Global());
        server.register(Constant.LOG_SYNC_PATH, handler);
        return handler;
    }
}
