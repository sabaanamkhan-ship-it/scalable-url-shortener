package com.sabaanam.urlshortener.codegen;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.shortcode")
public record ShortCodeProperties(
        long multiplierA,
        long multiplierB,
        long multiplierC,
        long offset
) {
}