package org.lee.hearbeat;

import org.lee.common.Constant;
import org.lee.rpc.Server;

public class HeartBeatReceiver {

    public void startListenHeartBeat(){
        Server.getInstance().register(Constant.HEART_BEAT_PATH,new HeartBeatHandler());
    }
}
