package org.lee.study.raft;

import org.lee.study.raft.util.NetUtil;

public class Server {
    public static void main(String[] args) throws Throwable {
        NetUtil.tcp(8080);
    }
}
