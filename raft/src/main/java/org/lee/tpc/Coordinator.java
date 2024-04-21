package org.lee.tpc;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Context;
import org.lee.common.utils.ColUtils;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.Endpoint;
import org.lee.log.domain.LogEntry;
import org.lee.log.domain.SyncResult;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Coordinator {
    ThreadPoolExecutor tpc = ThreadUtil.poolOfIO("tpc");
    private final Context context;

    public Coordinator(Context context) {
        this.context = context;
    }

    /**
     * start transaction and add log for  the transaction started
     *
     * @param data
     * @param servers
     */
    public void push(LogEntry data, List<Worker> servers) {
        AtomicInteger okResp = new AtomicInteger(0);
        AtomicInteger failResp = new AtomicInteger(0);
        ColUtils.foreach(tpc, servers, endpoint -> {
            // Do server agree write a data
            try {
                boolean prepared = endpoint.prepare(data);
                int i = prepared ? okResp.incrementAndGet() : failResp.incrementAndGet();
                log.info("endpoint:{}, succeed:{}", endpoint, prepared);
            } catch (Exception e) {
                failResp.incrementAndGet();
            }
        }).join();
        if (context.isMajority(okResp.get())) {
            AtomicInteger wroteCount = new AtomicInteger(0);
            AtomicInteger wroteFail = new AtomicInteger(0);
            ColUtils.foreach(tpc, servers, endpoint -> {
                try {
                    // write data
                    boolean b = endpoint.writeData(data);
                    log.info("endpoint:{}, write data:{}", endpoint, b);
                    if (b) {
                        wroteCount.incrementAndGet();
                        return;
                    }
                    wroteFail.incrementAndGet();
                } catch (Exception e) {
                    wroteFail.incrementAndGet();
                }
            });
        } else {
            ColUtils.foreach(tpc, servers, endpoint -> {
                // rollback data
                // for loop until rollback to success
                boolean b = endpoint.rollBack(data);
                log.info("endpoint:{}, rollback:{}", endpoint, b);
            });
        }
    }
}
