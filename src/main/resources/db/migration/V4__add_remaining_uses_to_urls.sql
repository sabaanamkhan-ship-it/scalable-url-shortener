ALTER TABLE urls ADD COLUMN remaining_uses INT NULL;

ALTER TABLE urls
    ADD CONSTRAINT chk_urls_remaining_uses
        CHECK (remaining_uses IS NULL OR remaining_uses >= 0);