package org.lee.common;

public class GlobalConfig {
    public GlobalConfig() {
    }

    private String masterHost = "localhost";
    private int masterPort = 80;
    private int currentPort = 80;
    private String currentHost = "localhost";


    public String getMasterHost() {
        return masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public void setCurrentPort(int currentPort) {
        this.currentPort = currentPort;
    }

    public int getCurrentPort() {
        return currentPort;
    }

    public String getCurrentHost() {
        return currentHost;
    }

    public String getCurrentAddr() {
        return getCurrentHost() + ":" + getCurrentPort();
    }

    public void setCurrentHost(String currentHost) {
        this.currentHost = currentHost;
    }
}
