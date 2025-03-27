package org.lee.log.service;

import org.lee.common.utils.JsonUtil;
import org.lee.log.domain.LogEntry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 现在的写入只支持 override 和 append
 */
public class MMapFile implements AutoCloseable {
    private FileChannel dataChannel;
    private MappedByteBuffer dataBuffer;
    private RandomAccessFile dataFile;


    private MappedByteBuffer metaBuffer;
    private FileChannel metaChannel;
    private static final int metaSize = 1 << 12; // 4K 1个系统内存页面

    private static final int size = 1 << 10; // 1G
    private int wroteSize = 0;

    public MMapFile(String filename) {
        File file = new File(filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            init(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init(File file) throws IOException {

        this.dataFile = new RandomAccessFile(file, "rw");
        this.dataChannel = dataFile.getChannel();
        this.dataBuffer = dataChannel.map(FileChannel.MapMode.READ_WRITE, 0, MMapFile.size);


        File meta = new File(file.getAbsoluteFile() + ".meta");
        if (!meta.exists()) {
            meta.createNewFile();
            RandomAccessFile metaFile = new RandomAccessFile(meta, "rw");
            this.metaChannel = metaFile.getChannel();
            this.metaBuffer = metaChannel.map(FileChannel.MapMode.READ_WRITE, 0, metaSize);
            metaWrite(0);
            flush();
        } else {
            RandomAccessFile metaFile = new RandomAccessFile(meta, "rw");
            this.metaChannel = metaFile.getChannel();
            this.metaBuffer = metaChannel.map(FileChannel.MapMode.READ_WRITE, 0, metaSize);
        }
        this.wroteSize = metaRead();
        recoveryOffset();

    }

    private void recoveryOffset() {
        this.dataBuffer.position(wroteSize);
    }

    public void append(ByteBuffer byteBuffer) {
        dataBuffer.put(byteBuffer);
        wroteSize += byteBuffer.limit();
        metaWrite(wroteSize);
    }


    public List<LogEntry> read() {

        int size = metaRead();

        int currentOffset = 0;
        List<LogEntry> logEntries = new ArrayList<>();
        MappedByteBuffer slice = dataBuffer.slice(0, wroteSize);
        slice.rewind();
        while (currentOffset < size) {
            int currentSize = slice.getInt();
            byte[] bytes = new byte[currentSize];
            slice.get(bytes);
            logEntries.add(JsonUtil.fromJson(bytes, LogEntry.class));
            currentOffset += currentSize + 4;
        }
        return logEntries;
    }

    public void flush() {
        dataBuffer.force();
        metaBuffer.force();
    }

    @Override
    public void close() throws Exception {
        if (dataChannel != null) {
            dataChannel.close();
        }
        if (dataFile != null) {
            dataFile.close();
        }
        if (metaChannel != null) {
            metaChannel.close();
        }
    }

    public Integer metaRead() {
        int anInt = metaBuffer.getInt(0);
        byte[] bytes = new byte[anInt];
        metaBuffer.get(4, bytes);
        return Integer.parseInt(new String(bytes));
    }

    public void metaWrite(int size) {
        byte[] bytes = (size + "").getBytes(StandardCharsets.UTF_8);
        metaBuffer.putInt(0, bytes.length);
        metaBuffer.put(4, bytes);
    }
}
