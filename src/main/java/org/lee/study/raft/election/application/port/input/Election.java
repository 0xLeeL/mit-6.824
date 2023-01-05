package org.lee.study.raft.election.application.port.input;

import org.lee.study.raft.election.application.domain.ElectionResult;

public interface Election {

    ElectionResult elect();
}