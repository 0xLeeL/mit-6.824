package org.lee.election.domain;

public record Propose(
        int epoch,
        int port // candidate's listening port
) {

}
