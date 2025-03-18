package org.lee.study.server.node.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lee.study.common.JsonUtil;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private String request;
    private String path;
    @Setter
    private Response response;

    public static Request ofClient(String path,Object t){
        return new Request(JsonUtil.toJson(t),path,null);
    }

}
