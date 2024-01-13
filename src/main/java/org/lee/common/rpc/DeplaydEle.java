package org.lee.common.rpc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DeplaydEle implements Delayed {
    private String name;
    private long startTime; // 延时时间

    public DeplaydEle(String name, long delay) {
        this.name = name;
        this.startTime = System.currentTimeMillis() + delay; // 设置延时时间
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MICROSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((DeplaydEle) o).startTime) {
            return -1;
        }
        if (this.startTime > ((DeplaydEle) o).startTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "DelayedElement{name='" + name + "', startTime=" + LocalDateTime.ofEpochSecond(startTime / 1000,0, ZoneOffset.ofHours(8)) + "}";
    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DeplaydEle> queue = new DelayQueue<>();

        // 向队列中添加元素
        queue.put(new DeplaydEle("Element 1", 5000)); // 5秒延时
        queue.put(new DeplaydEle("Element 2", 10000)); // 10秒延时

        // 从队列中取元素
        while (true) {
            DeplaydEle element = queue.take(); // 阻塞直到元素的延时过期
            System.out.println("取出: " + element);
        }
    }
}
