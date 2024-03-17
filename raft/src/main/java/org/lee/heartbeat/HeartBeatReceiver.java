package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.rpc.Server;

public class HeartBeatReceiver {

    private final Server listener;

    public HeartBeatReceiver(Server listener) {
        this.listener = listener;
    }

    public void startListenHeartBeat(){
        listener.register(Constant.HEART_BEAT_PATH,new HeartBeatHandler());
    }
}
