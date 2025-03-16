package org.lee.log.domain;

public record SendResult(
        boolean syncSucceed,
        int epoch,
        int indexOfEpoch
) {

    public static SendResult success() {
        return new SendResult(true, -1, -1);
    }

    public static SendResult fail(int epoch, int indexOfEpoch) {
        return new SendResult(false, epoch, indexOfEpoch);
    }

    public boolean failed(){
        return !syncSucceed;
    }
}
