package org.lee.log.domain;

public record SyncResult(
        boolean syncSucceed
//        ,
//        int epoch,
//        int indexOfEpoch
) {

    public static SyncResult success(){
        return new SyncResult(true);
    }
}
