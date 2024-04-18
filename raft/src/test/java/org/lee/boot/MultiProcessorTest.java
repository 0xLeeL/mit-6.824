package org.lee.boot;


import org.junit.jupiter.api.Test;
import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;
import org.lee.store.handler.DbGetDataHandler;
import org.lee.store.handler.DbPutDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.lee.boot.MultiProcessor.start;


public class MultiProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(MultiProcessorTest.class);

    public static void main(String[] args) {

        Context context = Context.builder().build();
        context.addEndpoint(new Endpoint(81,"localhost"));
        context.addEndpoint(new Endpoint(82,"localhost"));
        context.addEndpoint(new Endpoint(83,"localhost"));
        Bootstrap bootstrap = Bootstrap.builder().global(context);
        CompletableFuture<Void> sss = bootstrap.start();

        Server start = context.getServer();
        context.becomeMaster();

    }


}

