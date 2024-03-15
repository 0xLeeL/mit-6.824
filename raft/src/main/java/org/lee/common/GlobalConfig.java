package org.lee.common;

public class GlobalConfig {
    private GlobalConfig(){}
    private static String masterHost = "localhost";
    private static int masterPort = 80;

    public static String getMasterHost(){
        return masterHost;
    }
    public static int getMasterPort(){
        return masterPort;
    }

    public static void setMasterHost(String masterHost) {
        GlobalConfig.masterHost = masterHost;
    }

    public static void setMasterPort(int masterPort) {
        GlobalConfig.masterPort = masterPort;
    }
}
