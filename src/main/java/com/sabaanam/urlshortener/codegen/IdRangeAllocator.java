package com.sabaanam.urlshortener.codegen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdRangeAllocator {

    private static final String SEQUENCE_NAME = "url_id";

    private final IdBlockService idBlockService;
    private final int blockSize;

    private long next = 0;
    private long end = 0;

    public IdRangeAllocator(IdBlockService idBlockService,
                            @Value("${app.shortcode.block-size}") int blockSize) {
        this.idBlockService = idBlockService;
        this.blockSize = blockSize;
    }

    public synchronized long nextId() {
        if (next >= end) {
            long start = idBlockService.reserveBlock(SEQUENCE_NAME, blockSize);
            next = start;
            end = start + blockSize;
        }
        return next++;
    }
}