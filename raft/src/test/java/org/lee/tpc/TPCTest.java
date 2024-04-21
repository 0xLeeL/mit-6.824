package org.lee.tpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lee.common.Context;
import org.lee.election.Endpoint;
import org.lee.log.domain.LogEntry;

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
        Context context = new Context();
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
    void  test_write_fail() {
        Context context = new Context();
        context.addEndpoint(Endpoint.build("a:1"));
        context.addEndpoint(Endpoint.build("a:2"));
        context.addEndpoint(Endpoint.build("a:3"));
        var coordinator = new Coordinator(context);
        coordinator.push(
                new LogEntry(1, 2, "", "xx"),
                List.of(w1, w2, w1)
        );
    }
}
