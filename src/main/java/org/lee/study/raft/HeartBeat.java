package org.lee.study.raft;

import lombok.extern.slf4j.Slf4j;
import org.lee.study.raft.util.GlobalTimer;
import org.lee.study.raft.util.NetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class HeartBeat {
    // 需要发送心跳的客户端
    private final List<NetAddress> clients;
    private final Lock lock = new ReentrantLock();
    private final GlobalTimer globalTimer;
    private final int beatSecond = 5;


    public HeartBeat() {
        this.clients = new ArrayList<>(1024);
        globalTimer = new GlobalTimer();
    }


    public void add(NetAddress netAddress) {
        safe(() -> clients.add(netAddress));
    }

    public void addAll(Collection<NetAddress> netAddress) {
        netAddress.forEach(d -> safe(() -> clients.add(d)));
    }

    public void safe(Runnable runnable) {
        lock.lock();
        runnable.run();
        lock.unlock();
    }

    public void startUp() {
        globalTimer.bootPeriod(this::doBeat, TimeUnit.SECONDS.toMillis(beatSecond));
    }

    private void doBeat() {
        lock.lock();
        List<NetAddress> netAddresses = List.copyOf(clients);
        lock.unlock();
        if (netAddresses.isEmpty()) {
            log.warn("no client connected!!!");
        }
        netAddresses.forEach(addr -> {
            try {
                NetUtil.sendMessageByTcp(addr, "heart beat");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
