package org.lee.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lee.election.domain.CurrentActor;
import org.lee.election.Endpoint;
import org.lee.heartbeat.HeartBeatReceiver;
import org.lee.heartbeat.HeartBeatSender;
import org.lee.heartbeat.MasterStatus;
import org.lee.log.LogSyncer;
import org.lee.rpc.Server;
import org.lee.store.handler.DbGetDataHandler;
import org.lee.store.handler.DbPutDataHandler;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * runtime context
 */
@Builder
@AllArgsConstructor
@Data
@Slf4j
public class Context {
    @Builder.Default
    public MasterStatus masterStatus = MasterStatus.SUSPEND;
    @Builder.Default
    private CurrentActor currentActor = CurrentActor.NEW_NODE;

    private final Map<String,Endpoint> endpoints = new ConcurrentHashMap<>();
    private final AtomicInteger epoch = new AtomicInteger(0);
    private final AtomicInteger indexOfEpoch = new AtomicInteger(0);
    private Server server;
    private LogSyncer logSyncer;
    private HeartBeatSender heartBeatSender;
    private HeartBeatReceiver heartBeatReceiver;
    @Builder.Default
    private int acceptedEpoch = -1;

    private Timer timer;


    public Context() {
        currentActor = CurrentActor.NEW_NODE;
        masterStatus = MasterStatus.SUSPEND;
    }

//    public void removeEndpoint(Endpoint endpoint) {
//        endpoints.remove(endpoint.getAddr());
//    }

    public void addEndpoint(Endpoint endpoint) {
        endpoints.put(endpoint.addr(),endpoint);
    }

    public Endpoint getEndpoint(Endpoint endpoint) {
        return endpoints.get(endpoint.addr());
    }

    public Set<Endpoint> getEndpoints() {
        return Set.copyOf(endpoints.values());
    }


    public synchronized void setMasterStatus(MasterStatus masterStatus) {
        this.masterStatus = masterStatus;
    }

    public boolean masterIsHealth() {
        return MasterStatus.HEALTH.equals(masterStatus);
    }

    public synchronized void health() {
        setMasterStatus(MasterStatus.HEALTH);
    }

    public synchronized void downed() {
        setMasterStatus(MasterStatus.DOWNED);
    }

    public synchronized void suspend() {
        setMasterStatus(MasterStatus.SUSPEND);
    }

    public synchronized MasterStatus getMasterStatus() {
        return masterStatus;
    }

    public int getEpoch() {
        return epoch.get();
    }

    public int addEpoch() {
        return epoch.incrementAndGet();
    }

    public synchronized void updateActor(CurrentActor currentActor) {
        this.currentActor = currentActor;
        log.info("=====================================================================");
        log.info("=====================================================================");
        log.info("|| become a {} ",currentActor);
        log.info("=====================================================================");
        log.info("=====================================================================");
    }

    public synchronized int getAcceptedEpoch() {
        return acceptedEpoch;
    }

    public synchronized void setAcceptedEpoch(int acceptedEpoch) {
        this.acceptedEpoch = acceptedEpoch;
    }

    public boolean isMajority(int num) {
        return num >= getMajority();
    }
    public Integer getMajority() {
        return getEndpoints().size() / 2 + 1;
    }

    public int getIndexOfEpoch() {
        return indexOfEpoch.get();
    }

    public int incrementIndexOfEpoch() {
        return indexOfEpoch.incrementAndGet();
    }

    public CurrentActor getCurrentActor() {
        return currentActor;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    
    public void becomeMaster() {
        this.updateActor(CurrentActor.MASTER);
        if (timer==null){
//            timer = TimerUtils.masterAlive();
        }
        if (heartBeatReceiver==null) {
            this.heartBeatReceiver = new HeartBeatReceiver(server);
            heartBeatReceiver.startListenHeartBeat();
        }

        if (getHeartBeatSender() != null) {
            log.info("stop timer");
            getHeartBeatSender().stop();
            log.info("stoped timer");
        }

        log.info("stopped getLogSyncer");
        setMasterStatus(MasterStatus.HEALTH);
        server.getGlobalConfig().setMasterHost(getServer().getGlobalConfig().getCurrentHost());
        server.getGlobalConfig().setMasterPort(getServer().getGlobalConfig().getCurrentPort());
        server.register(Constant.PUT_DATA_PATH,new DbPutDataHandler(this));
        server.register(Constant.GET_DATA_PATH,new DbGetDataHandler());
    }

    /**
     * 1. start append entry listener
     * 2. stop heartbeat schedule
     */
    public void becomeFollower() {
        setMasterStatus(MasterStatus.HEALTH);
        this.updateActor(CurrentActor.FOLLOWER);
        LogSyncer.follow(getServer());

        if (getHeartBeatSender() != null) {
            getHeartBeatSender().schedule();
        }
        if (timer != null){
            timer.cancel();
        }
        server.register(Constant.PUT_DATA_PATH,new DbPutDataHandler(this));
        server.register(Constant.GET_DATA_PATH,new DbGetDataHandler());
    }
    public void becomeFollower(String masterHost,int masterPort) {
        becomeFollower();
        getServer().getGlobalConfig().setMasterPort(masterPort);
        getServer().getGlobalConfig().setMasterHost(masterHost);
    }

    public Endpoint getMaster(){
        return new Endpoint(server.getGlobalConfig().getMasterPort(),server.getGlobalConfig().getMasterHost(),CurrentActor.CANDIDATE.name());
    }

    public void updateMaster(Endpoint endpoint){
        GlobalConfig globalConfig = server.getGlobalConfig();
        globalConfig.setMasterHost(endpoint.host());
        globalConfig.setMasterPort(endpoint.port());
    }

    public boolean isMaster(){
        return CurrentActor.MASTER.equals(currentActor);
    }

    public Endpoint getSelf(){
        return new Endpoint(server.getGlobalConfig().getCurrentPort(),server.getGlobalConfig().getCurrentHost(),currentActor.name());
    }
}
