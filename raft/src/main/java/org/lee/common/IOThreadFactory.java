package org.lee.common;

import java.util.concurrent.ThreadFactory;

public class IOThreadFactory implements ThreadFactory {
    private final String threadName;

    public IOThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r,threadName);
    }

    public static ThreadFactory factory(String name){
        return new IOThreadFactory(name);
    }
}
