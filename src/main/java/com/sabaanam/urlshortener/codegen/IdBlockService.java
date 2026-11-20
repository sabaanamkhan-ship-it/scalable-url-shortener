package com.sabaanam.urlshortener.codegen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdBlockService {

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public long reserveBlock(String sequenceName, int blockSize) {
        IdSequence sequence = idSequenceRepository.findByNameForUpdate(sequenceName)
                .orElseThrow(() -> new IllegalStateException("Sequence not found: " + sequenceName));

        long start = sequence.getNextValue();
        sequence.setNextValue(start + blockSize);

        return start;
    }
}