package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.rpc.Handler;

public class HeartBeatHandler implements Handler {
    @Override
    public String handle(String requestJson) {
        return Constant.HEART_RESP;
    }
}
