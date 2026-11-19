CREATE TABLE users (
                       id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email         VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                       CONSTRAINT uk_users_email UNIQUE (email)
);