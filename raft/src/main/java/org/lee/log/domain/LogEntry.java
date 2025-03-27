package org.lee.log.domain;

import org.lee.common.Constant;
import org.lee.common.utils.JsonUtil;
import org.lee.store.domain.PutRequest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record LogEntry(
        Integer epoch,
        Integer epochIndex, // index of the epoch
        Object data,
        String path
) {
    public static final String DELIMITER = "\n";

    public static LogEntry ofPutData(int epoch,int epochIndex,Object data){
        return new LogEntry(epoch,epochIndex,data, Constant.PUT_DATA_PATH);
    }

    public boolean putData(){
        return Constant.PUT_DATA_PATH.equals(path());
    }
    public PutRequest tryConvert(){
        if (data instanceof PutRequest putRequest){
            return putRequest;
        }
        if (data instanceof Map<?,?>){
            return JsonUtil.fromJson(JsonUtil.toJson(data), PutRequest.class);
        }
        return null;
    }

    public ByteBuffer encode() {
        byte[] bytes = (JsonUtil.toJson(this)).getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        ByteBuffer allocate = ByteBuffer.allocate(4 + length);
        allocate.putInt(length);
        allocate.put(bytes);
        allocate.flip();
        return allocate;
    }
}
