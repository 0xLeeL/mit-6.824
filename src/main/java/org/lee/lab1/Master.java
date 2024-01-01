package org.lee.lab1;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Master<ARG,RESULT> {
    Map<String, Worker> onlineWorkers = new ConcurrentHashMap<>();
    Map<ARG, List<Worker>> wokers = new ConcurrentHashMap<>();

    public CompletableFuture<Void> submit(ARG arg,Function<ARG,RESULT> input){

        CompletableFuture<RESULT>[] array = partition(arg)
                .stream()
                .map(c -> CompletableFuture.supplyAsync(() -> input.apply(c)))
                .<CompletableFuture<RESULT>>toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(array);
    }


    public void registerWorker(Worker worker){

    }

    public List<ARG> partition(ARG arg){
        return List.of();
    }


}
