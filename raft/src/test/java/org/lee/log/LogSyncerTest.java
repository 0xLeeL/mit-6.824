package org.lee.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.election.Endpoint;
import org.lee.log.handler.LogSyncHandler;
import org.lee.rpc.Server;
import org.lee.util.StartServer;

public class LogSyncerTest {

    @Test
    void test_logSync() {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Server server1 = StartServer.start(p1);
        Server server2 = StartServer.start(p2);
        Server server3 = StartServer.start(p3);
        LogSyncHandler handler1 = LogSyncer.follow(server1);
        LogSyncHandler handler2 = LogSyncer.follow(server2);
        LogSyncHandler handler3 = LogSyncer.follow(server3);



        Context context = new Context();
        LogSyncer logSyncer = new LogSyncer(context);
        context.addEndpoint(new Endpoint(p1, "localhost"));
        context.addEndpoint(new Endpoint(p2, "localhost"));
        context.addEndpoint(new Endpoint(p3, "localhost"));
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
