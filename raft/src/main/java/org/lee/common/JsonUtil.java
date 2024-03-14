package org.lee.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonUtil {
    private final static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JavaTimeModule());
    }

    public static <T> String toJson(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T fromJson(String json,Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T fromJson(byte[] json,Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
