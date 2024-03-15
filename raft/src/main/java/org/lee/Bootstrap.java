package org.lee;

import org.lee.common.GlobalConfig;
import org.lee.hearbeat.HeartBeatReceiver;
import org.lee.rpc.Server;

public class Bootstrap {

    public void startServer(){
        int port = GlobalConfig.getMasterPort();
        Server server = new Server(port);
        Server.setInstance(server);
        HeartBeatReceiver receiver = new HeartBeatReceiver();
        receiver.startListenHeartBeat();
    }
}
