package org.lee.common.utils;

import org.lee.common.IOThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static ThreadPoolExecutor poolOfIO(String name) {
        int processors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                processors << 2,
                processors << 3,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                IOThreadFactory.factory(name),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
