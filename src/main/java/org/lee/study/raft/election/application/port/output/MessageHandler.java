package org.lee.study.raft.election.application.port.output;

import org.lee.study.raft.election.application.domain.Message;

public interface MessageHandler {

    Message receiveMessage();
    void sendMessage(Message message);
}
