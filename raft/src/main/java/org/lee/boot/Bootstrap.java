package org.lee.boot;

import org.lee.common.Context;
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
import java.util.Properties;


public class Bootstrap {
    private final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    private Context context;
    private GlobalConfig globalConfig;

    public Bootstrap() {
        if (context == null) {
            context = new Context();
        }
        if (globalConfig == null) {
            globalConfig = new GlobalConfig();
        }
    }


    public static Bootstrap builder() {
        return new Bootstrap();
    }

    public Bootstrap global(Context context) {
        this.context = context;
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

    @Deprecated
    public Server startServer() {
        Server start = Server.start();
        HeartBeatReceiver receiver = new HeartBeatReceiver(start);
        receiver.startListenHeartBeat();
        return start;
    }

    public Server start() {
        Server server = new Server(this.globalConfig,this.context);
        LogSyncer.follow(server);

        context.setServer(server);

        globalConfig.getServers().forEach(context::addEndpoint);
        log.info("servers is:{}", context.getEndpoints());
        Election electionRaft = new ElectionRaft(context, globalConfig);
        electionRaft.register(server);
        CurrentActor elect = electionRaft.elect();
        log.info("current status is:{}", elect.name());
        if (CurrentActor.MASTER.equals(elect)) {// master
            HeartBeatReceiver heartBeatReceiver = new HeartBeatReceiver(server);
            heartBeatReceiver.startListenHeartBeat();
            LogSyncer logSyncer = new LogSyncer(context);
            logSyncer.syncing();
        } else {
            HeartBeatSender heartBeatSender = new HeartBeatSender(context, globalConfig, electionRaft);
            heartBeatSender.schedule();
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
