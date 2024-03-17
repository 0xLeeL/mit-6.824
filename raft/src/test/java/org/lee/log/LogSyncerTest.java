package org.lee.log;

import org.junit.jupiter.api.Test;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;

public class LogSyncerTest {

    @Test
    void test_logSync() throws Exception {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Server server1 = Server.start(p1);
        Server server2 = Server.start(p2);
        Server server3 = Server.start(p3);
        LogSyncer.start(server1);
        LogSyncer.start(server2);
        LogSyncer.start(server3);



        Global global = new Global();
        GlobalConfig globalConfig = new GlobalConfig();
        LogSyncer logSyncer = new LogSyncer(global);
        global.addEndpoint(new Endpoint(p1, "localhost", global, globalConfig));
        global.addEndpoint(new Endpoint(p2, "localhost", global, globalConfig));
        global.addEndpoint(new Endpoint(p3, "localhost", global, globalConfig));
        logSyncer.sync("writing operation");
        logSyncer.sync("writing operation");
        logSyncer.sync("writing operation");
        logSyncer.sync("writing operation");
        logSyncer.sync("writing operation");
        logSyncer.sync("writing operation");

        server1.close();
        server2.close();
        server3.close();
    }

}
