package org.lee.log.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lee.log.domain.LogEntry;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class MMapFileTest {

    @Test
    void test_append_log_entry() throws Exception {
        MMapFile mmapFile = new MMapFile("demo.txt");
        mmapFile.append(LogEntry.ofPutData(2, 2, "test").encode());
        mmapFile.flush();
        mmapFile.close();
    }
    @Test
    void test_append_log_entry1() throws Exception {
        MMapFile mmapFile = new MMapFile("C:\\Users\\oo\\Desktop\\code\\java\\mit-6.824\\store3\\entry.bin");
        mmapFile.append(LogEntry.ofPutData(2, 2, "test").encode());
        mmapFile.flush();
        mmapFile.close();
    }

    @Test
    void test_read_log_entry() throws Exception {
        MMapFile mmapFile = new MMapFile("MMapFileTest-test_read_log_entry-demo-"+".txt");
        mmapFile.append(LogEntry.ofPutData(1, 2, "test").encode());
        mmapFile.flush();
        mmapFile.close();

        List<LogEntry> read = mmapFile.read();
        read.forEach(System.out::println);
        Assertions.assertNotNull(read);
    }

    @Test
    void test_read_log_entry_only() throws Exception {
        MMapFile mmapFile = new MMapFile("demo.txt");

        List<LogEntry> read = mmapFile.read();
        Assertions.assertNotNull(read);
        read.forEach(System.out::println);
    }
    @Test
    void test_read_log_entry_only_1() {
        MMapFile mmapFile = new MMapFile("store_83");

        List<LogEntry> read = mmapFile.read();
        Assertions.assertNotNull(read);
        read.forEach(System.out::println);
    }

    @Test
    void test_slice(){
        ByteBuffer allocate = ByteBuffer.allocate(100);
        allocate.putInt(1);
        allocate.putInt(2);
        allocate.putInt(3);
        allocate.putInt(4);
        System.out.println(allocate.position());    // 16
        System.out.println(allocate.limit());       // 100
        System.out.println(allocate.capacity());    // 100
        allocate.flip();
        System.out.println(allocate.position());          // 0
        System.out.println(allocate.limit());           // 100
        System.out.println(allocate.capacity());            // 100

    }
    @Test
    void test_slice_1(){
        ByteBuffer allocate = ByteBuffer.allocate(100);
        allocate.putInt(1);
        allocate.putInt(2);
        allocate.putInt(3);
        allocate.putInt(4);
        System.out.println(allocate.position());    // 16
        System.out.println(allocate.limit());       // 100
        System.out.println(allocate.capacity());    // 100

        allocate.flip();
        System.out.println("============================");
        System.out.println(allocate.position());          // 0
        System.out.println(allocate.limit());             // 100
        System.out.println(allocate.capacity());          // 100

        System.out.println("============================");
        System.out.println(allocate.getInt());            // 100

        System.out.println("============================");
        allocate.flip();
        System.out.println(allocate.position());          // 0
        System.out.println(allocate.limit());             // 100
        System.out.println(allocate.capacity());          // 100
        allocate.putInt(11);
//        allocate.putInt(22); // 会导致exception java.nio.BufferOverflowException
        System.out.println(allocate.position());          // 0
        System.out.println(allocate.limit());             // 4
        System.out.println(allocate.capacity());          // 100
    }

    @Test
    void test_slice_2(){
        ByteBuffer allocate = ByteBuffer.allocate(100);
        allocate.putInt(1);
        allocate.putInt(2);
        allocate.putInt(3);
        allocate.putInt(4);
        System.out.println(allocate.position());    // 16
        System.out.println(allocate.limit());       // 100
        System.out.println(allocate.capacity());    // 100

        ByteBuffer slice = allocate.slice();
        System.out.println(slice.position());    // 0
        System.out.println(slice.limit());       // 84
        System.out.println(slice.capacity());    // 84
    }

    @Test
    void test_slice_3(){
        ByteBuffer allocate = ByteBuffer.allocate(100);
        allocate.putInt(1);
        allocate.putInt(2);
        allocate.putInt(3);
        allocate.putInt(4);
        System.out.println(allocate.position());    // 16
        System.out.println(allocate.limit());       // 100
        System.out.println(allocate.capacity());    // 100
        allocate.flip();

        ByteBuffer slice = allocate.slice();
        System.out.println(slice.position());    // 16
        System.out.println(slice.limit());       // 100
        System.out.println(slice.capacity());    // 100
        for (int i = 0; i < 4; i++) {
            System.out.println(slice.getInt());
        }
    }

    @Test
    void test_slice_4(){
        ByteBuffer allocate = ByteBuffer.allocate(100);
        allocate.putInt(1);
        allocate.putInt(2);
        allocate.putInt(3);
        allocate.putInt(4);

        System.out.println(allocate.position());    // 16
        System.out.println(allocate.limit());       // 100
        System.out.println(allocate.capacity());    // 100
        allocate.flip();

        ByteBuffer slice = allocate.slice();

        System.out.println(slice.getInt());

        System.out.println(slice.position());    // 16
        System.out.println(slice.limit());       // 100
        System.out.println(slice.capacity());    // 100

        ByteBuffer slice1 = slice.slice();
        System.out.println(slice1.position());    // 16
        System.out.println(slice1.limit());       // 100
        System.out.println(slice1.capacity());    // 100
    }

    @Test
    void test_append() throws Exception {

        RandomAccessFile rw = new RandomAccessFile("a.txt", "rw");
        MappedByteBuffer map = rw.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 2048);
        byte[] bytes = "hello".getBytes();
        ByteBuffer allocate = ByteBuffer.allocate(bytes.length + 4);
        allocate.putInt(bytes.length);
        allocate.put(bytes);
        allocate.flip();
        map.put(allocate);
    }

    @Test
    void test_mmap_size_than_file_size() throws Exception {
        extracted();

//        RandomAccessFile rw = new RandomAccessFile("a.txt", "rw");
//        MappedByteBuffer map = rw.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 2048);
//        map.put("hello".getBytes());
    }

    private static void extracted() throws IOException {
        RandomAccessFile rw = new RandomAccessFile("a.txt", "rw");
        MappedByteBuffer map = rw.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 1024);
        ByteBuffer wrap = ByteBuffer.wrap("hello".getBytes());
        map.put(wrap);
        map.force();
        rw.close();
    }
}
