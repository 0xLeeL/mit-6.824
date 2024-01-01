package org.lee.lab1.boot;

import org.lee.lab1.Master;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lee.lab1.LAB1.masterPort;


public class MasterMain {
    public static void main(String[] args) throws InterruptedException {
        List<Long> collect = IntStream.range(0, 1 << 10).mapToLong(d -> d).boxed().collect(Collectors.toList());
        Master<Long, Long> master = new Master<>(masterPort);
        Thread.sleep(5000);
        Long submit = master.submit(
                collect,
                (li) -> {
                    int sum = 0;
                    for (Long aLong : li) {
                        sum += aLong;
                    }
                    return (long) sum;
                },
                (result) -> {
//                    log.info("{}", result);
                    return result.stream().mapToLong(d->d).sum();
                }
        );
        System.out.println(submit);
        System.out.println(collect.stream().mapToLong(d -> d).sum());
    }
}
