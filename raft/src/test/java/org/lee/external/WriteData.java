package org.lee.external;

import org.lee.common.Constant;
import org.lee.election.Endpoint;
import org.lee.store.domain.GetRequest;
import org.lee.store.domain.GetResult;
import org.lee.store.domain.PutRequest;
import org.lee.store.domain.PutResult;

public class WriteData {

    public static void main(String[] args) {
        Endpoint localhost = new Endpoint(82, "localhost");
        PutResult call = localhost.call(
                Constant.PUT_DATA_PATH,
                new PutRequest("aaa","bbb"),
                PutResult.class);
        System.out.println(call);
        GetResult getResult = localhost.call(
                Constant.PUT_DATA_PATH,
                new GetRequest("aaa"),
                GetResult.class);
        System.out.println(getResult);
    }
}
