package org.lee.study.raft.election.application.service;

import org.lee.study.raft.election.application.domain.ElectionResult;
import org.lee.study.raft.election.application.domain.Message;
import org.lee.study.raft.election.application.port.input.Election;
import org.lee.study.raft.election.application.port.output.MessageHandler;

import java.util.List;

public class ElectionService implements Election {
    private final MessageHandler messageHandler;

    public ElectionService(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public ElectionResult elect() {
        Message message = messageHandler.receiveMessage();
        String content = message.content();
        message.
        return new ElectionResult(null, null, List.of());
    }
}