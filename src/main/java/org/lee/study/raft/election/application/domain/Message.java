package org.lee.study.raft.election.application.domain;

import lombok.Builder;

/**
 * @param content 消息内容
 * @param type    消息
 */
@Builder
public record Message(
        MessageContent content,
        String type
) {

}
