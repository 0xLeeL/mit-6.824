package org.lee.study.raft.util;

import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {

    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T parse(InputStream inputStream, Class<T> clazz) throws IOException {
        byte[] bytes = new byte[10240];
        int len = inputStream.read(bytes, 0, bytes.length);
        String json = StringUtil.to(bytes, 0, len);
        return JSON.parseObject(json, clazz);
    }

    public static <T> String toJson(T object) {
        return JSON.toJSONString(object);
    }
}
