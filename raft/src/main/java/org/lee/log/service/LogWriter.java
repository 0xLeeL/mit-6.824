package org.lee.log.service;

import org.lee.log.domain.LogEntry;

import java.io.File;
import java.util.List;

/**
 * 基于 mmap file 写入文件
 */
public class LogWriter {
    private final String path;
    private final MMapFile mMapFile;

    public LogWriter(String path) {

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        this.mMapFile = new MMapFile(path + "/entry.bin");
        this.path = path;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                mMapFile.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void write(LogEntry logEntry) {
        mMapFile.append(logEntry.encode());
    }

    public List<LogEntry> read() {
        return mMapFile.read();
    }


    public void close() {
        try {
            mMapFile.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
