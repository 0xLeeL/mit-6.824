package org.lee.boot;

import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.Election;
import org.lee.election.domain.CurrentActor;
import org.lee.heartbeat.HeartBeatReceiver;
import org.lee.heartbeat.HeartBeatSender;
import org.lee.log.LogSyncer;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Bootstrap {
    private final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    private Global global;
    private GlobalConfig globalConfig;


    public static Bootstrap builder(){
        return new Bootstrap();
    }

    public Bootstrap global(Global global){
        this.global = global;
        return this;
    }
    public Bootstrap globalConfig(GlobalConfig globalConfig){
        this.globalConfig = globalConfig;
        return this;    }

    public Server startServer() {
        Server start = Server.start();
        HeartBeatReceiver receiver = new HeartBeatReceiver(start);
        receiver.startListenHeartBeat();
        return start;
    }

    public Server start() {
        Server server = new Server(this.globalConfig);
        Election election = new Election(global, server);
        CurrentActor elect = election.elect();
        log.info("current status is:{}",elect.name());
        if (CurrentActor.MASTER.equals(elect)){// master
            HeartBeatSender heartBeatSender = new HeartBeatSender(global,globalConfig);
            heartBeatSender.schedule();
            LogSyncer.follow(server);
        }else{
            HeartBeatReceiver heartBeatReceiver = new HeartBeatReceiver(server);
            heartBeatReceiver.startListenHeartBeat();
            LogSyncer logSyncer = new LogSyncer(global);
            logSyncer.syncing();
        }
        return server;
    }
}
