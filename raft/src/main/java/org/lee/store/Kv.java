package org.lee.store;

import org.lee.boot.Bootstrap;
import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Server;
import org.lee.store.handler.DbGetDataHandler;
import org.lee.store.handler.DbPutDataHandler;

import java.util.concurrent.CompletableFuture;

public class Kv {
    public static void main(String[] args) {
        Context context = Context.builder().build();
        context.addEndpoint(new Endpoint(81,"localhost", CurrentActor.NEW_NODE.name()));
        context.addEndpoint(new Endpoint(82,"localhost",CurrentActor.NEW_NODE.name()));
        context.addEndpoint(new Endpoint(83,"localhost",CurrentActor.NEW_NODE.name()));
        Bootstrap bootstrap = Bootstrap.builder().global(context);
        CompletableFuture<Void> sss = bootstrap.start();

        Server start = context.getServer();
        start.register(Constant.GET_DATA_PATH, new DbGetDataHandler());
        start.register(Constant.PUT_DATA_PATH, new DbPutDataHandler(context));
//        start.start().join();
    }
}
