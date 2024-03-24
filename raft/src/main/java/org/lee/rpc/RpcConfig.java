package org.lee.rpc;


public class RpcConfig {
    private int timeoutMill = 3_000;


    public static RpcConfig builder(){
        return new RpcConfig();
    }

    public int getTimeoutMill() {
        return timeoutMill;
    }

    public RpcConfig timeoutMill(int mill){
        this.timeoutMill = mill;
        return this;
    }
}
