package com.sabaanam.urlshortener.codegen;

public class Base62Encoder {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;
    public static final int CODE_LENGTH = 7;
    public static final long MAX_VALUE = 3_521_614_606_208L; // 62^7

    public String encode(long value) {
        if (value < 0 || value >= MAX_VALUE) {
            throw new IllegalArgumentException("Value out of range: " + value);
        }
        char[] chars = new char[CODE_LENGTH];
        for (int i = CODE_LENGTH - 1; i >= 0; i--) {
            chars[i] = ALPHABET.charAt((int) (value % BASE));
            value = value / BASE;
        }
        return new String(chars);
    }

    public long decode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("Code must be " + CODE_LENGTH + " characters");
        }
        long value = 0;
        for (char c : code.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index < 0) {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
            value = value * BASE + index;
        }
        return value;
    }
}