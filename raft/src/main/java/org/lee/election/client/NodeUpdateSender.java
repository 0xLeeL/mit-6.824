package org.lee.election.client;

import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.election.domain.NodeUpdate;
import org.lee.election.domain.NodeUpdateResult;

public class NodeUpdateSender {
    private static Integer tryTimes = 0;

    public static void updateNode(NodeUpdate nodeUpdate, Context context){
        try{
            if (tryTimes>10){
                return;
            }
            tryTimes++;
            NodeUpdateResult nodeUpdateResult = context.getMaster().updateNode(nodeUpdate);
            if (nodeUpdateResult.isRedirect()){
                context.updateMaster(nodeUpdateResult.getMasterEndPoint());
                updateNode(nodeUpdate,context);
                return;
            }
            tryTimes = 0;
        }catch (Exception e){
            Endpoint masterNode = findMasterNode(nodeUpdate, context);
            if (masterNode != null){
                context.updateMaster(masterNode);
                return;
            }
            updateNode(nodeUpdate,context);
        }
        if (tryTimes > 10){
            throw new RuntimeException("update node failed");
        }

    }

    private static Endpoint findMasterNode(NodeUpdate nodeUpdate, Context context) {
        for (Endpoint endpoint : context.getEndpoints()) {
            try {
                NodeUpdateResult nodeUpdateResult = endpoint.updateNode(nodeUpdate);
                if (nodeUpdateResult.isRedirect()){
                    return nodeUpdateResult.getMasterEndPoint();
                }
                if (nodeUpdateResult.isOK()){
                    tryTimes = 0;
                    return endpoint;
                }
            }catch (Exception e){
                // ingore;
                return null;
            }
        }
        return null;
    }
}
