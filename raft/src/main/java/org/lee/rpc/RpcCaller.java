package org.lee.rpc;

public interface RpcCaller<REQ, RES> {


     RES call(String path, REQ commend, Class<RES> resultClass);

    void connect();

    void onFailed();

    void close();
}
