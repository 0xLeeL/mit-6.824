package org.lee.tpc;

import org.lee.rpc.Handler;

public interface Worker {

    boolean prepare(Object data);
    boolean writeData(Object data);
    boolean rollBack(Object data);

}
