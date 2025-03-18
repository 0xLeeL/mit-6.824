package org.lee.election.handler;

import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.JsonUtil;
import org.lee.common.utils.ThreadUtil;
import org.lee.election.Endpoint;
import org.lee.election.domain.NodeUpdate;
import org.lee.election.domain.NodeUpdateResult;
import org.lee.rpc.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class NodeUpdateHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(NodeUpdateHandler.class);
    private final Context context;
    private final GlobalConfig config;

    public NodeUpdateHandler(Context context) {
        this.context = context;
        this.config = context.getServer().getGlobalConfig();
    }

    @Override
    public NodeUpdateResult handle(String requestJson) {
        NodeUpdate propose = JsonUtil.fromJson(requestJson, NodeUpdate.class);
        log.info("current epoch is {}, receive:{}", context.getEpoch(), propose);
        Endpoint endpoint = context.getEndpoint(propose.getEndpoint());
        if (endpoint.status().equals(propose.getEndpoint().status())) {
            return NodeUpdateResult.ok();
        }
        if (!context.isMaster() && propose.isFromMaster()) {
            context.addEndpoint(endpoint);
            return NodeUpdateResult.ok();
        }
        // 如果不是 master 节点，那么就需要重定向到 master 节点
        if (!context.isMaster() && propose.isFromNode()) {
            return NodeUpdateResult.redirect(context.getMaster());
        }
        return upgrade(propose);
    }

    /**
     * 这个是 master 需要进行配置升级
     *
     * @param propose
     * @return
     */
    private NodeUpdateResult upgrade(NodeUpdate propose) {
        /*
         * 这里存在配置升级过程中的一个并发问题。
         */
        Set<Endpoint> endpoints = context.getEndpoints();
        CountDownLatch count = new CountDownLatch(context.getMajority());
        for (Endpoint c : endpoints) {
            ThreadUtil.submit(() -> {
                NodeUpdateResult nodeUpdateResult = c.updateNode(propose);
                if (nodeUpdateResult != null) {
                    count.countDown();
                }
            });
        }
        try {
            count.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return NodeUpdateResult.failed();
        }

        // 模拟commit
        context.addEndpoint(propose.getEndpoint());

        return NodeUpdateResult.ok();
    }
}
