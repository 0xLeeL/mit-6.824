package org.lee.study.raft.election.application.domain;

import lombok.Builder;
import org.lee.study.raft.NetAddress;

import java.util.List;

/**
 * @param master 选举出的master
 * @param epoch  选举使用了好多个阶段
 * @param nodes  参与选举的节点
 */
@Builder
public record ElectionResult(
        NetAddress master,
        Integer epoch,
        List<NetAddress> nodes
) {

}
