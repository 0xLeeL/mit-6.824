package org.lee.election.client;

import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.election.domain.NodeUpdate;
import org.lee.election.domain.NodeUpdateResult;

public class NodeUpdateSender {
    private static Integer tryTimes = 0;

    public static void updateNode(NodeUpdate nodeUpdate, Context context){
        while (tryTimes < 10) {

            NodeUpdateResult nodeUpdateResult;
            try {
                nodeUpdateResult = context.getMaster().updateNode(nodeUpdate);
            } catch (Exception e) {
                nodeUpdateResult = findMasterNode(nodeUpdate, context);
            }

            if (nodeUpdateResult.isRedirect()){
                context.updateMaster(nodeUpdateResult.getMasterEndPoint());
            }

            if (nodeUpdateResult.isOK()) {
                context.addEndpoint(nodeUpdate.getEndpoint());
                return;
            }
        }
        throw new RuntimeException("update node failed");
    }


    private static NodeUpdateResult findMasterNode(NodeUpdate nodeUpdate, Context context) {
        for (Endpoint endpoint : context.getEndpoints()) {
            try {
                NodeUpdateResult nodeUpdateResult = endpoint.updateNode(nodeUpdate);
                if (nodeUpdateResult.isRedirect()){
                    return nodeUpdateResult;
                }
                if (nodeUpdateResult.isOK()){
                    tryTimes = 0;
                    return nodeUpdateResult;
                }
                return nodeUpdateResult;
            }catch (Exception e){
                // ingore;
                return NodeUpdateResult.failed();
            }
        }
        return NodeUpdateResult.failed();
    }
}
