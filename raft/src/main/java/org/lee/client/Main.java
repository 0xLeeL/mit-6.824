package org.lee.client;

import org.lee.common.Constant;
import org.lee.rpc.Client;
import org.lee.store.domain.PutRequest;

public class Main {
    public static void main(String[] args) {
        PutRequest putData = new PutRequest("456","xxx");
        Client<PutRequest, String> localhost = new Client<>("localhost", 82);
        localhost.connect();
        String call = localhost.call(Constant.PUT_DATA_PATH, putData, String.class);
        System.out.println(call);
    }
}
