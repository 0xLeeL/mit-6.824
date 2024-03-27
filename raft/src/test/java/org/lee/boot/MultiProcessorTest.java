package org.lee.boot;


import org.junit.jupiter.api.Test;
import org.lee.common.utils.ThreadUtil;
import org.lee.store.Kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.lee.boot.MultiProcessor.start;


public class MultiProcessorTest {
    private final Logger log = LoggerFactory.getLogger(MultiProcessorTest.class);

    @Test
    void test() throws IOException {
        Process process1 = start(MyApplication.class, "xxx xxx xxx ","sss");
        Process process2 = start(MyApplication.class, "xxx xxx xxx ","xxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        ThreadUtil.sleep(1000);
//        // 输出子进程的输出
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            log.info(line);
//        }
    }

    @Test
    void test_multi_sub() {
        String javaBin = "java"; // 构建java命令路径
        String classpath = System.getProperty("java.class.path"); // 获取类路径
        String mainClass1 = MyApplication.class.getCanonicalName(); // 主类1
        String mainClass2 = MyApplication.class.getCanonicalName(); // 主类2

        // 命令模板
        try {
            // 启动第一个Java程序
            Process start1 = new ProcessBuilder("cmd", "/c",
                    String.format("\"%s\" -Dlog.dir=aaa -cp \"%s\" %s", javaBin, classpath, mainClass1), "title", "windowTitle2")
                    .inheritIO()
                    .start();

            // 启动第二个Java程序
            Process start2 = new ProcessBuilder("cmd", "/c",
                    "cmd", "/k",
                    String.format("\"%s\" -Dlog.dir=bbb -cp \"%s\" %s", javaBin, classpath, mainClass2))
                    .inheritIO()
                    .start();


            ThreadUtil.sleep(1000);


            long pid2 = start2.pid();
            System.out.println("stop1"+pid2);
            new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid2)).start().waitFor();
            long pid1 = start1.pid();
            System.out.println("stop2"+pid1);
            new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid1)).start().waitFor();

            System.out.println("stopdddd");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
