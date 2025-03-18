package org.lee.boot;


import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.lee.boot.MultiProcessor.start;


public class MultiProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(MultiProcessorTest.class);

    public static void main(String[] args) {

        Context context = Context.builder().build();
        context.addEndpoint(new Endpoint(81,"localhost","new_node"));
        context.addEndpoint(new Endpoint(82,"localhost","new_node"));
        context.addEndpoint(new Endpoint(83,"localhost","new_node"));
        Bootstrap bootstrap = Bootstrap.builder().global(context);
        CompletableFuture<Void> sss = bootstrap.start();

        Server start = context.getServer();
        context.becomeMaster();

    }


}

