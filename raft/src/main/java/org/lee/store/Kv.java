package org.lee.store;

import org.lee.boot.Bootstrap;
import org.lee.common.Constant;
import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;
import org.lee.store.handler.DbGetDataHandler;
import org.lee.store.handler.DbPutDataHandler;

public class Kv {
    public static void main(String[] args) {
        Context context = Context.builder().build();
        context.addEndpoint(new Endpoint(81,"localhost"));
        context.addEndpoint(new Endpoint(82,"localhost"));
        context.addEndpoint(new Endpoint(83,"localhost"));
        Bootstrap bootstrap = Bootstrap.builder().global(context);
        Server start = bootstrap.start();
        start.register(Constant.GET_DATA_PATH, new DbGetDataHandler());
        start.register(Constant.PUT_DATA_PATH, new DbPutDataHandler());
    }
}
