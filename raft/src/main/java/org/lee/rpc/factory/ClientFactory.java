package org.lee.rpc.factory;

import org.lee.rpc.RpcCaller;
import org.lee.rpc.RpcConfig;
import org.lee.rpc.netty.ClientNetty;
import org.lee.rpc.socket.ClientSocket;

public class ClientFactory {

    public static <T,R> RpcCaller<T,R> ofSocket(String host,int port){
        return new ClientSocket<T, R>(host, port);
    }
    public static <T,R> RpcCaller<T,R> ofNetty(String host,int port){
        return new ClientNetty<>(host, port);
    }
}
