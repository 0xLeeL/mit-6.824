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



    private String masterHost;
    private int masterPort;
    @Builder.Default
    private int currentPort = 80;
    @Builder.Default
    private String currentHost = "localhost";
    @Builder.Default
    private int pingSeg = 1_000;
    @Builder.Default
    private int retryTimes = 2; // if retry 'retryTimes' heartbeat are failed, follower start to election
    @Builder.Default
    private Set<Endpoint> initServers = Set.of();

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
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
        config.setInitServers(new ConcurrentSkipListSet<>(
                Arrays.stream(configProperties.get("servers").split(";"))
                        .map(Endpoint::build)
                        .collect(Collectors.toSet())));
        return config;
    }
}
