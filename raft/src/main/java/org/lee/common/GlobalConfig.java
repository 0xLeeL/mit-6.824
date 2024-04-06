package org.lee.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lee.election.Endpoint;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

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
    @Builder.Default
    private Set<Endpoint> servers = Set.of();

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

    public static GlobalConfig parseProperties(Properties properties) {
        Map<String, String> configProperties = new HashMap<>();
        properties.forEach((k, v) -> configProperties.put(k.toString().toLowerCase(), v.toString()));
        GlobalConfig config = new GlobalConfig();
        config.setCurrentPort(Integer.parseInt(configProperties.get("CurrentPort".toLowerCase())));
        config.setCurrentHost(configProperties.get("CurrentHost".toLowerCase()));
        config.setRetryTimes(Integer.parseInt(configProperties.get("RetryTimes".toLowerCase())));
        config.setPingSeg(Integer.parseInt(configProperties.get("PingSeg".toLowerCase())));
        config.setServers(new ConcurrentSkipListSet<>(
                Arrays.stream(configProperties.get("servers").split(";"))
                        .map(Endpoint::build)
                        .collect(Collectors.toSet())));
        return config;
    }
}
