package org.lee.boot;

import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.Election;
import org.lee.election.ElectionRaft;
import org.lee.election.domain.CurrentActor;
import org.lee.heartbeat.HeartBeatReceiver;
import org.lee.heartbeat.HeartBeatSender;
import org.lee.log.LogSyncer;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Bootstrap {
    private final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    private Global global;
    private GlobalConfig globalConfig;

    public Bootstrap() {
        if (global == null) {
            global = new Global();
        }
        if (globalConfig == null) {
            globalConfig = new GlobalConfig();
        }
    }


    public static Bootstrap builder() {
        return new Bootstrap();
    }

    public Bootstrap global(Global global) {
        this.global = global;
        return this;
    }

    public Bootstrap globalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }


    public Bootstrap configFile(String filePath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(filePath));
            parseProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Bootstrap parseProperties(Properties properties){
        this.globalConfig = GlobalConfig.parseProperties(properties);
        return this;
    }

    public Server startServer() {
        Server start = Server.start();
        HeartBeatReceiver receiver = new HeartBeatReceiver(start);
        receiver.startListenHeartBeat();
        return start;
    }

    public Server start() {
        Server server = new Server(this.globalConfig);
        global.setServer(server);
        globalConfig.getServers().forEach(global::addEndpoint);
        log.info("servers is:{}", global.getEndpoints());
        Election electionRaft = new ElectionRaft(global, globalConfig);
        electionRaft.register(server);
        CurrentActor elect = electionRaft.elect();
        log.info("current status is:{}", elect.name());
        if (CurrentActor.MASTER.equals(elect)) {// master
            HeartBeatSender heartBeatSender = new HeartBeatSender(global, globalConfig, electionRaft);
            heartBeatSender.schedule();
            LogSyncer.follow(server);
        } else {
            HeartBeatReceiver heartBeatReceiver = new HeartBeatReceiver(server);
            heartBeatReceiver.startListenHeartBeat();
            LogSyncer logSyncer = new LogSyncer(global);
            logSyncer.syncing();
        }
        return server;
    }

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        String configFile = System.getProperty("config.file");
        bootstrap.configFile(configFile);
        bootstrap.start();
    }
}
