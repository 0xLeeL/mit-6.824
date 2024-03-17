package org.lee.election;

import org.junit.jupiter.api.Test;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.rpc.Server;

public class ElectionTest {

    @Test
    void test_election() throws Exception {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;
//        Server server1 = Server.start(p1);
//        Server server2 = Server.start(p2);
//        Server server3 = Server.start(p3);
//
//        Global global = new Global();
//        Election election1 = new Election(global,server1.getGlobalConfig());
//        election1.elect();
//        Election election2 = new Election(global);
//        election2.elect();
//        Election election3 = new Election(global);
//        election3.elect();
        Election e1 = getElection(p1);
        Election e2 = getElection(p2);
        Election e3 = getElection(p3);

        e1.elect();
        e2.elect();
        e3.elect();
        e1.getServer().close();
        e2.getServer().close();
        e3.getServer().close();
    }

    Election getElection(int port){
        Server server1 = Server.start(port);
        Global global = new Global();
        GlobalConfig globalConfig = server1.getGlobalConfig();
        global.addEndpoint(new Endpoint(81,"localhost",global,globalConfig));
        global.addEndpoint(new Endpoint(82,"localhost",global,globalConfig));
        global.addEndpoint(new Endpoint(83,"localhost",global,globalConfig));
        Election election1 = new Election(global,server1);
        return election1;
    }
}
