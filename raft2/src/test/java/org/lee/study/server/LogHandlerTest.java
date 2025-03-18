package org.lee.study.server;

import org.junit.jupiter.api.Test;
import org.lee.study.server.node.log.LogEntry;
import org.lee.study.server.node.log.LogHandler;

public class LogHandlerTest {
    @Test
    void test_handler_write(){
         LogHandler logHandler = new LogHandler();
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
         logHandler.handleLog(LogEntry.ofPutData(1,1, ""));
    }
}
