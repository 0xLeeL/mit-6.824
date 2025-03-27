package org.lee.util;

import org.lee.boot.Bootstrap;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;

import java.util.Set;

public class StartServer {

   public static Server start(int port,String logfile) {
       GlobalConfig globalConfig = new GlobalConfig();
       globalConfig.setStore(logfile);

       Context context = new Context(globalConfig);
       globalConfig.setInitServers(Set.of(
               new Endpoint(81, "localhost"),
               new Endpoint(82, "localhost"),
               new Endpoint(83, "localhost")));
       globalConfig.setCurrentPort(port);
       Server server = Bootstrap.builder().global(context).globalConfig(globalConfig)
               .init();
       return server;
    }
}
