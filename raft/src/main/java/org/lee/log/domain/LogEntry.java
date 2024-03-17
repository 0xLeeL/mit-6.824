package org.lee.log.domain;

public record LogEntry(
        Integer epoch,
        String data,
        Integer epochIndex // index of the epoch
) {
}
