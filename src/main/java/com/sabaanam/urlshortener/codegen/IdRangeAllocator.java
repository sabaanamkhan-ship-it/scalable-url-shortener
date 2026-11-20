package com.sabaanam.urlshortener.codegen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdRangeAllocator {

    private static final String SEQUENCE_NAME = "url_id";
    private static final int BLOCK_SIZE = 1000;

    private final IdBlockService idBlockService;

    private long next = 0;
    private long end = 0;

    public synchronized long nextId() {
        if (next >= end) {
            long start = idBlockService.reserveBlock(SEQUENCE_NAME, BLOCK_SIZE);
            next = start;
            end = start + BLOCK_SIZE;
        }
        return next++;
    }
}