package org.lee;

import org.lee.election.Election;
import org.lee.hearbeat.HeartBeatReceiver;
import org.lee.rpc.Server;

public class Bootstrap {

    public Server startServer(){
        Server start = Server.start();
        HeartBeatReceiver receiver = new HeartBeatReceiver();
        receiver.setListener(start);
        receiver.startListenHeartBeat();
        return start;

    }
}
