CREATE TABLE id_sequence (
                             name       VARCHAR(50) PRIMARY KEY,
                             next_value BIGINT      NOT NULL
);

INSERT INTO id_sequence (name, next_value) VALUES ('url_id', 56800235584);