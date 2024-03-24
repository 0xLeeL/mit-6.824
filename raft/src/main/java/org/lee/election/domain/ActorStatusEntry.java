package org.lee.election.domain;


public record ActorStatusEntry(
        String host,
        Integer port,
        String actor
) {

    public boolean sameServer(String host, Integer port){
        return this.host.equals(host) && this.port.equals(port);
    }
}
