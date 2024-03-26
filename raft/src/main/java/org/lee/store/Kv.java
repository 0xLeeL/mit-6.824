package org.lee.store;

import org.lee.boot.Bootstrap;
import org.lee.common.Constant;
import org.lee.common.Global;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;
import org.lee.store.handler.DbGetDataHandler;
import org.lee.store.handler.DbPutDataHandler;

public class Kv {
    public static void main(String[] args) {
        Global global = Global.builder().build();
        global.addEndpoint(new Endpoint(81,"localhost"));
        global.addEndpoint(new Endpoint(82,"localhost"));
        global.addEndpoint(new Endpoint(83,"localhost"));
        Bootstrap bootstrap = Bootstrap.builder().global(global);
        Server start = bootstrap.start();
        start.register(Constant.GET_DATA_PATH, new DbGetDataHandler());
        start.register(Constant.PUT_DATA_PATH, new DbPutDataHandler());
    }
}
