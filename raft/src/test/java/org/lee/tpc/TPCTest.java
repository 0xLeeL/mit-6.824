package org.lee.tpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lee.common.Context;
import org.lee.common.GlobalConfig;
import org.lee.common.utils.JsonUtil;
import org.lee.election.Endpoint;
import org.lee.log.domain.LogEntry;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.store.service.DBServiceInConcurrentHashMap;
import org.lee.tpc.handler.RollbackDataHandler;
import org.lee.tpc.handler.WorkerPrepareHandler;
import org.lee.tpc.handler.WorkerWriteDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TPCTest {
    Worker w1;
    Worker w2;

    @BeforeEach
    void setup() {
        w1 = new Worker() {
            @Override
            public boolean prepare(Object data) {
                return false;
            }

            @Override
            public boolean writeData(Object data) {
                return false;
            }

            @Override
            public boolean rollBack(Object data) {
                return false;
            }
        };
        w2 = new Worker() {
            @Override
            public boolean prepare(Object data) {
                return true;
            }

            @Override
            public boolean writeData(Object data) {
                return true;
            }

            @Override
            public boolean rollBack(Object data) {
                return true;
            }
        };
    }

    @Test
    void test_write_success() {
        GlobalConfig globalConfig = new GlobalConfig();
        Context context = new Context(globalConfig);
        context.addEndpoint(Endpoint.build("a:1"));
        context.addEndpoint(Endpoint.build("a:2"));
        context.addEndpoint(Endpoint.build("a:3"));
        var coordinator = new Coordinator(context);
        coordinator.push(
                new LogEntry(1, 2, "", "xx"),
                List.of(w1, w2, w2)
        );
    }

    @Test
    void test_write_fail() {
        GlobalConfig globalConfig = new GlobalConfig();
        Context context = new Context(globalConfig);
        context.addEndpoint(Endpoint.build("a:1"));
        context.addEndpoint(Endpoint.build("a:2"));
        context.addEndpoint(Endpoint.build("a:3"));
        var coordinator = new Coordinator(context);
        coordinator.push(
                new LogEntry(1, 2, "", "xx"),
                List.of(w1, w2, w1)
        );
    }

    @Test
    void test_handler() {

        GlobalConfig globalConfig = new GlobalConfig();
        Context context = new Context(globalConfig);
        context.addEndpoint(Endpoint.build("a:1"));
        context.addEndpoint(Endpoint.build("a:2"));
        context.addEndpoint(Endpoint.build("a:3"));
        var coordinator = new Coordinator(context);
        coordinator.push(
                new LogEntry(1, 2, new PutRequest("xx", "√√√"), "xx"),
                List.of(worker(), worker(), worker())
        );
    }

    @Test
    void test_handler_in_rpc() {

        GlobalConfig globalConfig = new GlobalConfig();
        Context context = new Context(globalConfig);
        context.addEndpoint(Endpoint.build("a:1"));
        context.addEndpoint(Endpoint.build("a:2"));
        context.addEndpoint(Endpoint.build("a:3"));
        var coordinator = new Coordinator(context);
        coordinator.push(
                new LogEntry(1, 2, new PutRequest("xx", "√√√"), "xx"),
                List.of(worker(), worker(), worker())
        );
    }

    public Worker worker() {
        return new Worker() {
            private final Logger log = LoggerFactory.getLogger(Worker.class);
            DBServiceUseCase db = new DBServiceInConcurrentHashMap();
            private RollbackDataHandler rollbackDataHandler = new RollbackDataHandler(db);
            private WorkerPrepareHandler workerPrepareHandler = new WorkerPrepareHandler(db);
            private WorkerWriteDataHandler workerWriteDataHandler = new WorkerWriteDataHandler(db);

            @Override
            public boolean prepare(Object data) {
                log.info("prepare received:{}", data);
                try {
                    Object handle = workerPrepareHandler.handle(JsonUtil.toJson(data));
                    return Boolean.TRUE.equals(handle);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return false;
                }
            }

            @Override
            public boolean writeData(Object data) {
                log.info("writeData received:{}", data);
                Object handle = workerWriteDataHandler.handle(JsonUtil.toJson(data));
                return Boolean.TRUE.equals(handle);
            }

            @Override
            public boolean rollBack(Object data) {
                log.info("rollBack received:{}", data);
                Object handle = rollbackDataHandler.handle(JsonUtil.toJson(data));
                return Boolean.TRUE.equals(handle);
            }
        };
    }
}
