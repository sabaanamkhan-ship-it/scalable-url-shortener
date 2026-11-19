CREATE TABLE urls (
                      id              BIGINT        PRIMARY KEY,
                      short_code      VARCHAR(16)   NOT NULL,
                      long_url        VARCHAR(2048) NOT NULL,
                      long_url_hash   CHAR(64)      NOT NULL,
                      user_id         BIGINT        NULL,
                      is_custom_alias BOOLEAN       NOT NULL DEFAULT FALSE,
                      is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
                      created_at      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                      expires_at      DATETIME(3)   NULL,
                      CONSTRAINT uk_urls_short_code UNIQUE (short_code),
                      CONSTRAINT fk_urls_user FOREIGN KEY (user_id) REFERENCES users (id),
                      CONSTRAINT chk_urls_expiry CHECK (expires_at IS NULL OR expires_at > created_at)
);

CREATE INDEX idx_urls_expires_at ON urls (expires_at);
CREATE INDEX idx_urls_user_longurl ON urls (user_id, long_url_hash);