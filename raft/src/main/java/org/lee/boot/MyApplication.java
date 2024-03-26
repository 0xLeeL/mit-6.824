package org.lee.boot;

import java.util.Arrays;

public class MyApplication {
    public static void main(String[] args) {
        String classpath = System.getProperty("java.class.path"); // 获取类路径
        Arrays.stream(args).forEach(System.out::println);
        System.out.println(classpath);
    }
}
