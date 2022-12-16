package org.lee.study.raft.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalTimer {
    Timer timer = new Timer();

    public void bootPeriod(Runnable runnable,long millSecond){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, new Date(), millSecond);
    }
}
