package org.lee.log.domain;

public record SyncResult(
        boolean syncSucceed
) {

    public static SyncResult success(){
        return new SyncResult(true);
    }
}
