package org.lee.election.domain;

public record ProposeResult(
        ProposeResultContent msg,
        boolean accept
) {
    public static ProposeResult acceptPropose() {
        return new ProposeResult(null, true);
    }

    public static ProposeResult refuseProposeWithoutMaster() {
        return new ProposeResult(new ProposeResultContent(false, null, null), false);
    }
    public static ProposeResult refuseProposeWithMaster(String host,Integer port) {
        return new ProposeResult(new ProposeResultContent(true, host, port), false);
    }
}
