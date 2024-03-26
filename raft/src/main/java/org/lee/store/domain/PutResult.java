package org.lee.store.domain;

public record PutResult(
        String key,
        Object value
) {
}
