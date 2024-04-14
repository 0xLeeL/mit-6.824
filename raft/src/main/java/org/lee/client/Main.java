package org.lee.client;

import org.lee.common.Constant;
import org.lee.rpc.Client;
import org.lee.store.domain.PutRequest;
import org.lee.store.domain.PutResult;

public class Main {
    public static void main(String[] args) {
        PutRequest putData = new PutRequest("456","xxx");
        Client<PutRequest, PutResult> localhost = new Client<>("localhost", 82);
        localhost.connect();
        PutResult call = localhost.call(Constant.PUT_DATA_PATH, putData, PutResult.class);
        System.out.println(call);
    }
}
