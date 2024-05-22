package org.lee.rpc.nio;


import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.rpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * The class listen command from client(or other server),and response result
 */
public class ServerNIO extends Server {


    private final Logger log = LoggerFactory.getLogger(ServerNIO.class);

    public ServerNIO(int listenPort) {
        super(listenPort);
    }

    public ServerNIO(GlobalConfig globalConfig, Context context) {
        super(globalConfig, context);
    }


    @Override
    public void close() throws Exception {

    }

    @Override
    public CompletableFuture<Void> listen() {
        return null;
    }
}
