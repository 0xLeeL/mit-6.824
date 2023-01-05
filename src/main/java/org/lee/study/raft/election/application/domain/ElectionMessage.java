package org.lee.study.raft.election.application.domain;

import org.lee.study.raft.NetAddress;

/**
 * 投票消息
 */
public record ElectionMessage(
        Integer epoch,
        NetAddress electedMater
        ) implements MessageContent{
}
