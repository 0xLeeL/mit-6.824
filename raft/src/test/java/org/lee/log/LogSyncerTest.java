package org.lee.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.Endpoint;
import org.lee.rpc.Server;

public class LogSyncerTest {

    @Test
    void test_logSync() {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Server server1 = Server.start(p1);
        Server server2 = Server.start(p2);
        Server server3 = Server.start(p3);
        SyncHandler handler1 = LogSyncer.start(server1);
        SyncHandler handler2 = LogSyncer.start(server2);
        SyncHandler handler3 = LogSyncer.start(server3);



        Global global = new Global();
        GlobalConfig globalConfig = new GlobalConfig();
        LogSyncer logSyncer = new LogSyncer(global);
        global.addEndpoint(new Endpoint(p1, "localhost", global, globalConfig));
        global.addEndpoint(new Endpoint(p2, "localhost", global, globalConfig));
        global.addEndpoint(new Endpoint(p3, "localhost", global, globalConfig));
        int size = 6;
        for (int i = 0; i < 6; i++) {
            logSyncer.sync("writing operation");
        }

//        server1.getGlobalConfig()
        Assertions.assertEquals(size, handler1.getEntries().size());
        Assertions.assertEquals(size, handler2.getEntries().size());
        Assertions.assertEquals(size, handler3.getEntries().size());

        server1.close();
        server2.close();
        server3.close();
    }

}
