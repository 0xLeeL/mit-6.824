package org.lee.election.domain;

public record ProposeResultContent(
        boolean existMaster,
        String host,
        Integer port
) {
}
