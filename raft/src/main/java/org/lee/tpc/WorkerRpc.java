package org.lee.tpc;

import org.lee.common.Constant;
import org.lee.rpc.Client;

public class WorkerRpc implements Worker{
    private final Client client;

    public WorkerRpc(String host, Integer port) {
        client = new Client(host,port);
    }

    @Override
    public boolean prepare(Object data) {
        try {
            client.connect();
            client.call(Constant.TPC_PREPARE, data, String.class);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean writeData(Object data) {
        try {
            client.connect();
            client.call(Constant.TPC_WRITE, data, String.class);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean rollBack(Object data) {
        try {
            client.connect();
            client.call(Constant.TPC_WRITE, data, String.class);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
