package org.lee.study.raft;

public class NetAddress {
    private final int port;
    private final String hostName;

    public NetAddress(int port, String hostName) {
        this.port = port;
        this.hostName = hostName;
    }
    public NetAddress(String hostName, int port) {
        this.port = port;
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }
}
