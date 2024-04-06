package org.lee.common.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {

    public static Timer schedule(Runnable runnable,int delay, int period){

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };

        // 定时任务开始执行后延迟1秒执行，之后每隔2秒执行一次
        timer.schedule(task, delay, period);
        return timer;
    }
}
