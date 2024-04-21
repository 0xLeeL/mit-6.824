package org.lee.tpc;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Context;
import org.lee.common.utils.ColUtils;
import org.lee.common.utils.ThreadUtil;
import org.lee.log.domain.LogEntry;

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
    public PushResult push(LogEntry data, List<Worker> servers) {
        AtomicInteger prepareOk = new AtomicInteger(0);
        AtomicInteger prepareFailed = new AtomicInteger(0);
        ColUtils.foreach(tpc, servers, endpoint -> {
            // Do server agree write a data
            try {
                boolean prepared = endpoint.prepare(data);
                int i = prepared ? prepareOk.incrementAndGet() : prepareFailed.incrementAndGet();
                log.info("endpoint:{}, succeed:{}", endpoint, prepared);
            } catch (Exception e) {
                prepareFailed.incrementAndGet();
            }
        }).join();
        AtomicInteger wroteCount = new AtomicInteger(0);
        AtomicInteger wroteFail = new AtomicInteger(0);
        if (context.isMajority(prepareOk.get())) {
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
        }

        if (context.isMajority(prepareFailed.get()) || context.isMajority(wroteFail.get())){
            ColUtils.foreach(tpc, servers, endpoint -> {
                // rollback data
                // for loop until rollback to success
                boolean b = endpoint.rollBack(data);
                log.info("endpoint:{}, rollback:{}", endpoint, b);
            });
            return PushResult.ROLLBACK_SUCCESS;
        }
        return PushResult.SUCCESS;
    }
}
