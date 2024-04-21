package org.lee.common.utils;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ColUtils {
    public static <T> CompletableFuture<Void> foreach(
            Executor executor,
            Collection<T> objects,
            Consumer<T> consumer
    ) {
       return  CompletableFuture.allOf(objects.stream().map(c -> {
            return CompletableFuture.runAsync(() -> {
                consumer.accept(c);
            }, executor);
        }).toArray(CompletableFuture[]::new));
    }
}
