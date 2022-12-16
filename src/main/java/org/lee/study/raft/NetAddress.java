package org.lee.study.raft;

public class NetAddress {
    private final int port;
    private final String hostName;

    private NetAddress(int port, String hostName) {
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

    public static NetAddress localAddr(int port) {
        return new NetAddress("localhost", port);
    }

    public static NetAddress remoteAddr(String hostName, int port) {
        return new NetAddress(hostName, port);
    }
}
