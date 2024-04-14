package org.lee.client;

import org.lee.client.domain.Config;
import org.lee.client.service.OperationImpl;
import org.lee.store.domain.PutResult;

public class Main {
    public static void main(String[] args) {
        OperationImpl localhost = new OperationImpl(Config.builder().host("localhost").port(81).build());
//        PutResult put = localhost.put("xxx", "xxxx");
        String s = localhost.get("xxx");
        System.out.println("get:" + s);
//        System.out.println(put);
    }
}
