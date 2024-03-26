package org.lee.election;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.common.Global;
import org.lee.common.GlobalConfig;
import org.lee.common.Pair;
import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Server;

import java.util.List;

public class ElectionRaftTest {

    @Test
    void test_election() {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Pair<ElectionRaft,Server> e1 = getElection(p1);
        Pair<ElectionRaft,Server> e2 = getElection(p2);
        Pair<ElectionRaft,Server> e3 = getElection(p3);

        CurrentActor er1 = e1.getFirst().elect();
        CurrentActor er2 = e2.getFirst().elect();
        CurrentActor er3 = e3.getFirst().elect();
        List<CurrentActor> er11 = List.of(er1, er2, er3);
        Assertions.assertTrue(er11.contains(CurrentActor.FOLLOWER));
        Assertions.assertTrue(er11.contains(CurrentActor.MASTER));
        Assertions.assertEquals(2,er11.stream().filter(c->c.equals(CurrentActor.FOLLOWER)).count());
        e1.getSecond().close();
        e2.getSecond().close();
        e3.getSecond().close();
    }

    Pair<ElectionRaft,Server> getElection(int port){
        Server server1 = Server.start(port);
        Global global = new Global();
        GlobalConfig globalConfig = server1.getGlobalConfig();
        global.addEndpoint(new Endpoint(81,"localhost"));
        global.addEndpoint(new Endpoint(82,"localhost"));
        global.addEndpoint(new Endpoint(83,"localhost"));
        ElectionRaft electionRaft1 = new ElectionRaft(global,globalConfig);
        electionRaft1.register(server1);
        return Pair.of(electionRaft1, server1);
    }
}
