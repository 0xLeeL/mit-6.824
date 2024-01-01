package org.lee.lab1.boot;

import org.lee.lab1.Worker;

import java.util.concurrent.CompletableFuture;

import static org.lee.lab1.LAB1.masterPort;
import static org.lee.lab1.LAB1.worker1;

public class WorkerMain1 {
    public static void main(String[] args) throws InterruptedException {

        CompletableFuture.runAsync(() -> new Worker<>(masterPort, worker1));
        Thread.sleep(100000000);
    }
}
