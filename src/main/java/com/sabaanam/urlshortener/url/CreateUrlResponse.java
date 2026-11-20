package com.sabaanam.urlshortener.url;

import java.time.LocalDateTime;

public record CreateUrlResponse(
        String shortCode,
        String shortUrl,
        String longUrl,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}