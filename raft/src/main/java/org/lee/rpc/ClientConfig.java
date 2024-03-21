package org.lee.rpc;


public class ClientConfig {
    private int timeoutMill = 3_000;


    public static ClientConfig builder(){
        return new ClientConfig();
    }

    public int getTimeoutMill() {
        return timeoutMill;
    }

    public ClientConfig timeoutMill(int mill){
        this.timeoutMill = mill;
        return this;
    }
}
