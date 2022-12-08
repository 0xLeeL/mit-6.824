package org.lee.study.raft.util;

import java.nio.charset.StandardCharsets;

public class StringUtil {

    public static String to(byte[] bytes,int offset, int length){
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }


}
