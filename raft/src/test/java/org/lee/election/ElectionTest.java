package org.lee.election;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Server;

import java.util.List;

public class ElectionTest {

    @Test
    void test_election() {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Election e1 = getElection(p1);
        Election e2 = getElection(p2);
        Election e3 = getElection(p3);

        CurrentActor er1 = e1.elect();
        CurrentActor er2 = e2.elect();
        CurrentActor er3 = e3.elect();
        List<CurrentActor> er11 = List.of(er1, er2, er3);
        Assertions.assertTrue(er11.contains(CurrentActor.FOLLOWER));
        Assertions.assertTrue(er11.contains(CurrentActor.MASTER));
        Assertions.assertEquals(2,er11.stream().filter(c->c.equals(CurrentActor.FOLLOWER)).count());
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
