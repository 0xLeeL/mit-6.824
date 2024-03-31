package org.lee.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class MultiProcessor {
    public static <T> Process start(Class<T> cls, String configFilePath, String logDir) {
        try {
            // 构建命令：假设MyApplication和ProcessStarter在同一类路径下
            String classpath = System.getProperty("java.class.path"); // 获取类路径
            String className = cls.getCanonicalName(); // 指定要运行的主类
            String logDirArg = String.format("-Dlog.dir=%s", logDir);
            String configFleArg = String.format("-Dconfig.file=%s", configFilePath);
            List<String> command = List.of(
                    "java",
                    configFleArg.trim(),
                    logDirArg.trim(),
                    "-cp",
                    classpath.trim(),
                    className.trim()
            );


            log.info("{}", String.join(" ", command));
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            Process start = processBuilder.start();
            log.info("pid:{} is started", start.pid());
            return start;
        } catch (IOException e) {
            return null;
        }
    }

    public static void exitProcess(Long pid) {
        ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid));
        try {
            Process process = processBuilder.start();
            // 等待命令执行完成
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                log.info("Process killed successfully.");
            } else {
                // 读取并输出错误信息
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
