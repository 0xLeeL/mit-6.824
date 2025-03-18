package org.lee.election.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.lee.election.Endpoint;
@Data
@NoArgsConstructor
public class NodeUpdate {
    private String from;
    private Endpoint endpoint;

    public NodeUpdate(Endpoint endpoint,String from) {
        this.endpoint = endpoint;
        this.from = from;
    }

    public static NodeUpdate ofNode(Endpoint endpoint){
        return new NodeUpdate(endpoint,"node");
    }
    public static NodeUpdate ofMaster(Endpoint endpoint){
        return new NodeUpdate(endpoint,"master");
    }

    public boolean isFromNode(){
        return "node".equals(from);
    }

    public boolean isFromMaster(){
        return "master".equals(from);
    }
}
