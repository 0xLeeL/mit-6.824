package org.lee.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MultiProcessor {
    public static <T> Process start(Class<T> cls, String configFilePath, String logdir) {
        try {
            // 构建命令：假设MyApplication和ProcessStarter在同一类路径下
            String classpath = System.getProperty("java.class.path"); // 获取类路径
            String className = cls.getCanonicalName(); // 指定要运行的主类

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-Dlog.dir=" + logdir, "-cp", classpath, className, configFilePath);

            return processBuilder.start();
        } catch (IOException e) {
            return null;
        }
    }
}
