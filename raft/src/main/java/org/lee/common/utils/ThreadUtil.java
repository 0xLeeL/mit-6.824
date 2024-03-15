package org.lee.common.utils;

public class ThreadUtil {

    public static void sleep(int mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
