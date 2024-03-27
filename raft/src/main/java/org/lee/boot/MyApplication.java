package org.lee.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MyApplication {
    private static final Logger log = LoggerFactory.getLogger(MyApplication.class);
    public static void main(String[] args) {
        String classpath = System.getProperty("java.class.path"); // 获取类路径
        Arrays.stream(args).forEach(log::info);
        Arrays.stream(classpath.split("\\.")).forEach(log::info);
    }
}
