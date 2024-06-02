package org.lee.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.lee.common.utils.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RpcUtil {

    private RpcUtil() {
    }

    public static int byteToInt(byte[] bytes) {
        return ((0xFF & bytes[0]) << 24) |
                ((0xFF & bytes[1]) << 16) |
                ((0xFF & bytes[2]) << 8) |
                (0xFF & bytes[3]);// if the byte is negative number ,  the -128(1000 0000) will be converted to -128(1111(x8) 1000 0000)
    }

    public static byte[] intToByte(int len) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (len >> 24);
        bytes[1] = (byte) (len >> 16);
        bytes[2] = (byte) (len >> 8);
        bytes[3] = (byte) (len);
        return bytes;
    }


    public static <T> void sendObj(T obj, OutputStream outputStream) throws IOException {
        String json = JsonUtil.toJson(obj);
        byte[] bytes = json.getBytes();
        int length = bytes.length;
        outputStream.write(intToByte(length));
        outputStream.write(bytes);
        outputStream.flush();
    }

    public static <T> void sendRequest(String path,T obj, OutputStream outputStream) throws IOException {
        byte[] pathBytes = path.getBytes();
        outputStream.write(intToByte(pathBytes.length));
        outputStream.write(pathBytes);

        byte[] objBytes = JsonUtil.toJson(obj).getBytes();
        outputStream.write(intToByte(objBytes.length));
        outputStream.write(objBytes);
        outputStream.flush();
    }


    public static String readToString(InputStream inputStream) throws IOException {
        byte[] lenBytes = inputStream.readNBytes(4);
        int len = RpcUtil.byteToInt(lenBytes);
        byte[] pathBytes = inputStream.readNBytes(len);
        return new String(pathBytes, StandardCharsets.UTF_8);
    }
    public static <T> T readToObject(InputStream inputStream,Class<T> cls) throws IOException {
        byte[] lenBytes = inputStream.readNBytes(4);
        int len = RpcUtil.byteToInt(lenBytes);
        byte[] dataBytes = inputStream.readNBytes(len);
        if (cls == String.class){
            return (T) new String(dataBytes);
        }
        return JsonUtil.fromJson(dataBytes,cls);
    }

    public static String readToString(ByteBuf byteBuf) {
        byte[] lenBytes = new byte[4];
        byteBuf.readBytes(lenBytes);
        int len = RpcUtil.byteToInt(lenBytes);
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes,0,len);
        return new String(bytes, StandardCharsets.UTF_8);
    }
     
    public static void write(ByteBuf buffer, Object obj) {
        String send = (obj instanceof String)? (String) obj : JsonUtil.toJson(obj);
        byte[] bytes = send.getBytes(StandardCharsets.UTF_8);
        buffer.writeBytes(RpcUtil.intToByte(bytes.length));
        buffer.writeBytes(bytes);
    }

}
