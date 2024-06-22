package org.lee.tpc;

import org.lee.common.Constant;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.factory.ClientFactory;
import org.lee.rpc.socket.ClientSocket;

public class WorkerRpc implements Worker {
    private final RpcCaller clientSocket;

    public WorkerRpc(String host, Integer port) {
        clientSocket = ClientFactory.ofNetty(host, port);
    }

    @Override
    public boolean prepare(Object data) {
        try {
            clientSocket.connect();
            clientSocket.call(Constant.TPC_PREPARE, data, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean writeData(Object data) {
        try {
            clientSocket.connect();
            clientSocket.call(Constant.TPC_WRITE, data, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean rollBack(Object data) {
        try {
            clientSocket.connect();
            clientSocket.call(Constant.TPC_WRITE, data, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
