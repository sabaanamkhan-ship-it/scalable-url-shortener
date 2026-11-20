package com.sabaanam.urlshortener.url;

import com.sabaanam.urlshortener.codegen.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class UrlService {

    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlRepository shortUrlRepository;
    private final String baseUrl;

    public UrlService(ShortCodeGenerator shortCodeGenerator,
                      ShortUrlRepository shortUrlRepository,
                      @Value("${app.base-url}") String baseUrl) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.shortUrlRepository = shortUrlRepository;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public CreateUrlResponse shorten(CreateUrlRequest request) {
        ShortCodeGenerator.GeneratedCode generated = shortCodeGenerator.generate();

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(generated.id());
        shortUrl.setShortCode(generated.shortCode());
        shortUrl.setLongUrl(request.longUrl());
        shortUrl.setLongUrlHash(sha256(request.longUrl()));
        shortUrl.setCreatedAt(LocalDateTime.now());

        ShortUrl saved = shortUrlRepository.save(shortUrl);

        return new CreateUrlResponse(
                saved.getShortCode(),
                baseUrl + "/" + saved.getShortCode(),
                saved.getLongUrl(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}