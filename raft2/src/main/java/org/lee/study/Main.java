package org.lee.study;

import org.lee.study.server.NettyUtils;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture.runAsync(()->{
            try {
                NettyUtils.http(8081);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture.runAsync(()->{
            try {
                NettyUtils.http(8082);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        NettyUtils.http(8080);
    }
}