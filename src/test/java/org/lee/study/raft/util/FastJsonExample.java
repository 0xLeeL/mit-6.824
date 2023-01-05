package org.lee.study.raft.util;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.lee.study.raft.NetAddress;

public class FastJsonExample {


    @Test
    void testJson(){
        String localhost = JSON.toJSONString(new NetAddress("localhost", 8080));
        System.out.println(localhost);
        Object parse = JSON.parseObject("{\"port\":8080, \"hostName\":\"sssss\"}", NetAddress.class);
        System.out.println(parse);

    }
}
