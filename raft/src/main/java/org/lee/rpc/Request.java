package org.lee.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lee.common.utils.JsonUtil;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private String request;
    private String path;
    private Response response;

    public static Request ofClient(String path,Object t){
        return new Request(JsonUtil.toJson(t),path,null);
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
