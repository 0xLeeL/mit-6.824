package org.lee.heartbeat.handler;

import lombok.extern.slf4j.Slf4j;
import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.rpc.Handler;

@Slf4j
public class HeartBeatHandler implements Handler {
    private final Context context;

    public HeartBeatHandler(Context context) {
        this.context = context;
    }

    @Override
    public String handle(String requestJson) {
        log.info("{} received: {}", context.getCurrentActor(), requestJson);

        return Constant.HEART_RESP;
    }
}
