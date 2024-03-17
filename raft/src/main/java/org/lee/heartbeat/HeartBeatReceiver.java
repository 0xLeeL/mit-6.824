package org.lee.heartbeat;

import org.lee.common.Constant;
import org.lee.rpc.Server;

public class HeartBeatReceiver {

    private Server listener;


    public void setListener(Server listener) {
        this.listener = listener;
    }

    public void startListenHeartBeat(){
        listener.register(Constant.HEART_BEAT_PATH,new HeartBeatHandler());
    }
}
