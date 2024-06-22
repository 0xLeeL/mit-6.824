package org.lee.client.service;

import org.lee.client.core.Operation;
import org.lee.client.domain.Config;
import org.lee.common.Constant;
import org.lee.election.Endpoint;
import org.lee.rpc.RpcCaller;
import org.lee.rpc.RpcConfig;
import org.lee.rpc.factory.ClientFactory;
import org.lee.rpc.netty.ClientNetty;
import org.lee.rpc.socket.ClientSocket;
import org.lee.store.domain.GetRequest;
import org.lee.store.domain.PutRequest;
import org.lee.store.domain.PutResult;


public class OperationImpl implements Operation {
    private final Config config;

    public OperationImpl(Config config) {
        this.config = config;
    }

    @Override
    public PutResult put(String key, Object data) {

        PutRequest putData = new PutRequest(key, data);
        PutResult call = call(putData, PutResult.class);
        if (call.isRedirect()) {
            Endpoint e = call.redirect();
            config.setHost(e.host());
            config.setPort(e.port());
            return call(putData, PutResult.class);
        }
        return call;
    }

    @Override
    public String get(String key) {
        GetRequest getRequest = new GetRequest(key);
        RpcCaller<GetRequest, String> localhost = ClientFactory.ofNetty(config.getHost(), config.getPort());
        localhost.connect();
        return localhost.call(Constant.GET_DATA_PATH, getRequest, String.class);
    }

    private <T, R> R call(T t, Class<R> cls) {
        RpcCaller<T, R> localhost = ClientFactory.ofNetty(config.getHost(), config.getPort());
        localhost.connect();
        return localhost.call(Constant.PUT_DATA_PATH, t, cls);
    }

}
