package org.lee.election.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lee.election.Endpoint;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeUpdateResult {
    private String result;
    private Endpoint masterEndPoint;
    private final static String redirect = "redirect";

    public static NodeUpdateResult ok() {
        return new NodeUpdateResult("ok",null);
    }
    public static NodeUpdateResult failed() {
        return new NodeUpdateResult("failed",null);
    }
    public static NodeUpdateResult redirect(Endpoint masterEndPoint) {
        return new NodeUpdateResult(redirect, masterEndPoint);
    }

    public boolean isRedirect(){
        return redirect.equals(result);
    }

    public boolean isOK() {
        return "ok".equals(result);
    }
}
