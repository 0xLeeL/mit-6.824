package org.lee.common;

import org.lee.election.domain.CurrentActor;
import org.lee.election.Endpoint;
import org.lee.heartbeat.MasterStatus;
import org.lee.rpc.Server;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * runtime context
 */
public class Global {
    public MasterStatus masterStatus = MasterStatus.SUSPEND;
    private CurrentActor currentActor = CurrentActor.CANDIDATE;

    private final Set<Endpoint> endpoints = new ConcurrentSkipListSet<>();
    private final AtomicInteger epoch = new AtomicInteger(0);
    private final AtomicInteger indexOfEpoch = new AtomicInteger(0);
    private Server server;
    private int acceptedEpoch = -1;

    public void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
    }

    public void addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    public Set<Endpoint> getEndpoints() {
        return Set.copyOf(endpoints);
    }


    public synchronized void setMasterStatus(MasterStatus masterStatus) {
        this.masterStatus = masterStatus;
    }

    public boolean masterIsHealth(){
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

    public synchronized void updateActor(CurrentActor currentActor){
        this.currentActor = currentActor;
    }

    public synchronized int getAcceptedEpoch() {
        return acceptedEpoch;
    }

    public synchronized void setAcceptedEpoch(int acceptedEpoch) {
        this.acceptedEpoch = acceptedEpoch;
    }

    public boolean isMajority(int num){
        return num > getEndpoints().size() / 2;
    }

    public int getIndexOfEpoch() {
        return indexOfEpoch.get();
    }
    public int incrementIndexOfEpoch(){
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
}
