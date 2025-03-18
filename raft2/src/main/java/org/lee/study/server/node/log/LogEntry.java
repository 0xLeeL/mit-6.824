package org.lee.study.server.node.log;

public record LogEntry(
        Integer epoch,
        Integer epochIndex, // index of the epoch
        Object data
) {

    public static LogEntry ofPutData(int epoch, int epochIndex, Object data) {
        return new LogEntry(epoch, epochIndex, data);
    }

}
