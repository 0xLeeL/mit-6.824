package org.lee.study.server.node.log;

import io.netty.channel.Channel;
import org.lee.study.common.JsonUtil;

import java.io.*;

public class LogHandler {
    private final String logPath;
    private final OutputStream outputStream;

    public LogHandler() {
        this.logPath = "node.log";
        try {
            new File(logPath).createNewFile();
            this.outputStream = new FileOutputStream(logPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleLog(LogEntry logEntry) {
        try {
            outputStream.write((JsonUtil.toJson(logEntry) + "\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendLog(LogEntry logEntry, Channel channel) {
        try {
            channel.writeAndFlush(JsonUtil.toJson(logEntry));
            outputStream.write((JsonUtil.toJson(logEntry) + "\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
