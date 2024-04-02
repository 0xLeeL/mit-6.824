package org.lee.log.domain;

public record SyncResult(
        boolean syncSucceed,
        int epoch,
        int indexOfEpoch
) {

    public static SyncResult success() {
        return new SyncResult(true, -1, -1);
    }

    public static SyncResult fail(int epoch, int indexOfEpoch) {
        return new SyncResult(false, epoch, indexOfEpoch);
    }

    public boolean failed(){
        return !syncSucceed;
    }
}
