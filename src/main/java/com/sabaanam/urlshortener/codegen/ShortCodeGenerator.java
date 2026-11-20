package com.sabaanam.urlshortener.codegen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShortCodeGenerator {

    private final IdRangeAllocator idRangeAllocator;
    private final CodeShuffler codeShuffler;
    private final Base62Encoder base62Encoder;

    public GeneratedCode generate() {
        long id = idRangeAllocator.nextId();
        long shuffled = codeShuffler.shuffle(id);
        String code = base62Encoder.encode(shuffled);
        return new GeneratedCode(id, code);
    }

    public record GeneratedCode(long id, String shortCode) {
    }
}