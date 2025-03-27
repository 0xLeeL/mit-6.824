package org.lee.election;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.boot.Bootstrap;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.Pair;
import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Server;
import org.lee.util.StartServer;

import java.util.List;
import java.util.Set;

public class ElectionRaftTest {

    @Test
    void test_election() throws Exception {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Pair<ElectionRaft, Server> e1 = getElection(p1);
        Pair<ElectionRaft, Server> e2 = getElection(p2);
        Pair<ElectionRaft, Server> e3 = getElection(p3);

        CurrentActor er1 = e1.getFirst().elect();
        CurrentActor er2 = e2.getFirst().elect();
        CurrentActor er3 = e3.getFirst().elect();
        List<CurrentActor> er11 = List.of(er1, er2, er3);
        Assertions.assertTrue(er11.contains(CurrentActor.FOLLOWER));
        Assertions.assertTrue(er11.contains(CurrentActor.MASTER));
        Assertions.assertEquals(2, er11.stream().filter(c -> c.equals(CurrentActor.FOLLOWER)).count());
        e1.getSecond().close();
        e2.getSecond().close();
        e3.getSecond().close();
    }

    /**
     *
     */
    @Test
    void test_election_join() throws Exception {
        int p1 = 81;
        int p2 = 82;
        int p3 = 83;

        Pair<ElectionRaft, Server> e1 = getElection(p1);
        Pair<ElectionRaft, Server> e2 = getElection(p2);
        Pair<ElectionRaft, Server> e3 = getElection(p3);

        CurrentActor er1 = e1.getFirst().elect();
        CurrentActor er2 = e2.getFirst().elect();
        CurrentActor er3 = e3.getFirst().elect();
        List<CurrentActor> er11 = List.of(er1, er2, er3);
        Assertions.assertTrue(er11.contains(CurrentActor.FOLLOWER));
        Assertions.assertTrue(er11.contains(CurrentActor.MASTER));
        Assertions.assertEquals(2, er11.stream().filter(c -> c.equals(CurrentActor.FOLLOWER)).count());
        if (er1.equals(CurrentActor.FOLLOWER)) {
            e1.getSecond().close();
            launchNew(p1);
        } else if (er2.equals(CurrentActor.FOLLOWER)) {
            e2.getSecond().close();
            launchNew(p2);
        } else if (er3.equals(CurrentActor.FOLLOWER)) {
            e3.getSecond().close();
            launchNew(p3);
        }
        e1.getSecond().close();
        e2.getSecond().close();
        e3.getSecond().close();
    }

    void launchNew(int port) throws Exception {
        Pair<ElectionRaft, Server> newE1 = getElection(port);
        CurrentActor elect = newE1.getFirst().elect();
        Assertions.assertEquals(CurrentActor.FOLLOWER, elect);
        newE1.getSecond().close();
    }

    Pair<ElectionRaft, Server> getElection(int port) {
        Server server = StartServer.start(port,"logFile");
        ElectionRaft electionRaft1 = new ElectionRaft(server.getContext(), server.getGlobalConfig());
        electionRaft1.register(server);
        return Pair.of(electionRaft1, server);
    }
}
