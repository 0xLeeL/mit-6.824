package org.lee.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class IOThreadFactory implements ThreadFactory {
    private final String threadName;

    public IOThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                log.warn(t.getMessage(), t);
            }
        }, threadName);
    }

    public static ThreadFactory factory(String name) {
        return new IOThreadFactory(name);
    }
}
