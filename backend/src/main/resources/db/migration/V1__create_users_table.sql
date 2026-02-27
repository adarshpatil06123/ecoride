-- V1: Create users table
CREATE TABLE users (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT (UUID()),
    name             VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    department       VARCHAR(100),
    year             INT          CHECK (year BETWEEN 1 AND 6),
    trust_score      INT          NOT NULL DEFAULT 0,
    rides_completed  INT          NOT NULL DEFAULT 0,
    carbon_credits   INT          NOT NULL DEFAULT 0,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
