package org.lee.study.raft;

import org.lee.study.raft.util.NetUtil;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws Throwable {
        NetUtil.sendMessageByTcp(new InetSocketAddress("localhost",8080),"测试测试测试");
    }


}
