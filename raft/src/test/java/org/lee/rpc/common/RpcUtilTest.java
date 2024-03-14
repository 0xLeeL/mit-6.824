package org.lee.rpc.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RpcUtilTest {

    private final Logger log = LoggerFactory.getLogger(RpcUtilTest.class);

    @Test
    void test_int_byte() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            Assertions.assertEquals(i, RpcUtil.byteToInt(RpcUtil.intToByte(i)));
        }
    }

    @Test
    void test_readToString() throws IOException {
        String testStr = "testStr";
        byte[] bytes = testStr.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = RpcUtil.intToByte(bytes.length);
        byte[] resultByte = new byte[bytes1.length + bytes.length];
        int i = 0;
        for (; i < bytes1.length; i++) {
            resultByte[i] = bytes1[i];
        }
        for (; i < bytes.length + bytes1.length; i++) {
            resultByte[i] = bytes[i - bytes1.length];
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                resultByte
        );

        String s = RpcUtil.readToString(byteArrayInputStream);
        Assertions.assertEquals(testStr, s);
    }
}
