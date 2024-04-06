package org.lee.boot;


import org.junit.jupiter.api.Test;
import org.lee.common.utils.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import static org.lee.boot.MultiProcessor.start;


public class MultiProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(MultiProcessorTest.class);

    public static void main(String[] args) {

        Process process1 = start(Bootstrap.class, "C:\\Users\\oo\\Desktop\\code\\java\\mit-6.824\\raft\\src\\main\\resources\\server1.properties", "server1");
        Process process2 = start(Bootstrap.class, "C:\\Users\\oo\\Desktop\\code\\java\\mit-6.824\\raft\\src\\main\\resources\\server2.properties", "server2");
        Process process3 = start(Bootstrap.class, "C:\\Users\\oo\\Desktop\\code\\java\\mit-6.824\\raft\\src\\main\\resources\\server3.properties", "server3");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                // 程序会在这里暂停，等待用户输入
                String userInput = scanner.nextLine();
                Set<String> q = Set.of("q", "exit", "stop");
                log.info("received:{}", userInput);
                if (q.contains(userInput)) {
                    MultiProcessor.exitProcess(process1.pid());
                    MultiProcessor.exitProcess(process2.pid());
                    MultiProcessor.exitProcess(process3.pid());
                    System.exit(-1);
                }
            }
        }).start();
        ThreadUtil.sleep(1000000);
    }


}

