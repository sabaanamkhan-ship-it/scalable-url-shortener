package com.sabaanam.urlshortener.codegen;

public class CodeShuffler {

    private static final long M = Base62Encoder.MAX_VALUE; // 62^7
    private static final int BASE = 62;
    private static final int DIGITS = Base62Encoder.CODE_LENGTH;

    private final long multiplierA;
    private final long multiplierB;
    private final long multiplierC;
    private final long offset;

    public CodeShuffler(long multiplierA, long multiplierB, long multiplierC, long offset) {
        validateMultiplier(multiplierA);
        validateMultiplier(multiplierB);
        validateMultiplier(multiplierC);
        if (offset < 0 || offset >= M) {
            throw new IllegalArgumentException("Offset out of range");
        }
        this.multiplierA = multiplierA;
        this.multiplierB = multiplierB;
        this.multiplierC = multiplierC;
        this.offset = offset;
    }

    public long shuffle(long value) {
        if (value < 0 || value >= M) {
            throw new IllegalArgumentException("Value out of range: " + value);
        }
        long x = (value * multiplierA) % M;
        x = reverseDigits(x);
        x = ((x * multiplierB) % M + offset) % M;
        x = reverseDigits(x);
        x = (x * multiplierC) % M;
        return x;
    }

    private long reverseDigits(long x) {
        long reversed = 0;
        for (int i = 0; i < DIGITS; i++) {
            reversed = reversed * BASE + (x % BASE);
            x = x / BASE;
        }
        return reversed;
    }

    private static void validateMultiplier(long m) {
        if (m <= 0 || m >= 2_000_000 || m % 2 == 0 || m % 31 == 0) {
            throw new IllegalArgumentException("Invalid multiplier: " + m);
        }
    }
}