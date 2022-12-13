package org.lee.study.raft;

import lombok.extern.slf4j.Slf4j;
import org.lee.study.raft.util.NetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
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
        List<NetAddress> netAddresses = List.copyOf(clients);
        netAddresses.forEach(addr->{
            try {
                NetUtil.sendMessageByTcp(addr,"");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
