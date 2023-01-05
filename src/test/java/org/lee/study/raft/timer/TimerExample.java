package org.lee.study.raft.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerExample {
    private static final Logger log = LoggerFactory.getLogger(TimerExample.class);
    public static void main(String[] args) {
        System.out.println("??????????????????????");
        Timer timer = new Timer();
        log.info(" 开始 执行");

        // 方法一：在指定多少毫秒之后执行任务 Timer#schedule(TimerTask, long)
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              log.info("-------------task execute 我的天啦--------------");
            }
        },1000 * 10 );

        // 方法二：制定再某一个具体时间执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("-------------task execute date 指定--------------");
            }
        },new Date());

        // 方法四：指定具体的时间开始，之后后固定评率执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("----------- 指定延时毫秒数后固定评率执行 -------------- ");
            }
        }, 5000, 2000);
        // 方法三：指定延时后固定评率执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("----------- 指定具体的时间开始，之后后固定评率执行 -------------- ");
            }
        }, new Date(), 2000);
    }
}
