package org.lee.boot;


import org.junit.jupiter.api.Test;
import org.lee.store.Kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.lee.boot.MultiProcessor.start;


public class MultiProcessorTest {
    private final Logger log =  LoggerFactory.getLogger(MultiProcessorTest.class);

    @Test
    void test() throws IOException {
        Process process = start(MyApplication.class,"xxx xxx xxx ");

        // 输出子进程的输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
    }
}
