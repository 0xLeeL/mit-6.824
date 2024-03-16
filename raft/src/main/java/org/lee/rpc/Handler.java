package org.lee.rpc;

public interface Handler {
    Object handle(String requestJson);
}
