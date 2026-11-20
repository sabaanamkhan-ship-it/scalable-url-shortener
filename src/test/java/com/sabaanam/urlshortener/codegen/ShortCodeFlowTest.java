package com.sabaanam.urlshortener.codegen;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ShortCodeFlowTest {


    @Autowired
    private IdRangeAllocator idRangeAllocator;

    @Autowired
    private CodeShuffler codeShuffler;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private ShortCodeGenerator shortCodeGenerator;

    @Autowired
    private IdSequenceRepository idSequenceRepository;

    @Test
    void showFullFlow_stepByStep() {
        for (int i = 1; i <= 3; i++) {
            long id = idRangeAllocator.nextId();
            long shuffled = codeShuffler.shuffle(id);
            String code = base62Encoder.encode(shuffled);

            System.out.println("URL " + i + ":  id = " + id
                    + "  ->  shuffled = " + shuffled
                    + "  ->  code = " + code);

            assertThat(code).hasSize(7);
        }

        long dbNextValue = idSequenceRepository.findById("url_id")
                .orElseThrow()
                .getNextValue();
        System.out.println("DB mein next_value ab: " + dbNextValue);
    }

    @Test
    void generate_thousandCodes_allUnique() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            codes.add(shortCodeGenerator.generate().shortCode());
        }

        assertThat(codes).hasSize(1000);
    }
}
