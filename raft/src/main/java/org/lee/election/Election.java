package org.lee.election;

import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Server;

public interface Election {


    CurrentActor elect();

    void register(Server server);
}
