package org.lee.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Request {
    private String request;
    private String path;
    private Response response;
}
