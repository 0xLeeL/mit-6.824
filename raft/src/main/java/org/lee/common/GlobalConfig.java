package org.lee.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalConfig {


    @Builder.Default
    private String masterHost = "localhost";
    @Builder.Default
    private int masterPort = 80;
    @Builder.Default
    private int currentPort = 80;
    @Builder.Default
    private String currentHost = "localhost";
    @Builder.Default
    private int pingSeg = 1_000;
    @Builder.Default
    private int retryTimes = 2; // if retry 'retryTimes' heartbeat are failed, follower start to election


    public String getMasterHost() {
        return masterHost;
    }

    public int getMasterPort() {
        return masterPort;
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
}
