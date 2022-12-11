package org.lee.study.raft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HeartBeat {
    // 需要发送心跳的客户端
    private final List<NetAddress> clients;
    private final Lock lock = new ReentrantLock();

    public HeartBeat(List<NetAddress> clients) {
        this.clients = new ArrayList<>(1024);
    }



    public  void add(NetAddress netAddress){
        safe(()-> clients.add(netAddress));
    }

    public void safe(Runnable runnable){
        lock.lock();
        runnable.run();
        lock.unlock();
    }

    public void doBeat(){

    }
}
