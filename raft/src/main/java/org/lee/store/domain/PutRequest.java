package org.lee.store.domain;

public record PutRequest(
        String key,
        Object value
) {
}
