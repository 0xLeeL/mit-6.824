package org.lee.election.domain;

public record ProposeResult(
        String msg,
        boolean accept
) {
    public static ProposeResult acceptPropose() {
        return new ProposeResult("success",true);
    }
    public static ProposeResult refusePropose() {
        return new ProposeResult("success",false);
    }
}
