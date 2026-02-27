-- V3: Create trust_connections table
CREATE TABLE trust_connections (
    user1_id          VARCHAR(36) NOT NULL,
    user2_id          VARCHAR(36) NOT NULL,
    mutual_ride_count INT         NOT NULL DEFAULT 1,
    PRIMARY KEY (user1_id, user2_id),
    CHECK (user1_id < user2_id),
    CONSTRAINT fk_tc_user1 FOREIGN KEY (user1_id) REFERENCES users(id),
    CONSTRAINT fk_tc_user2 FOREIGN KEY (user2_id) REFERENCES users(id)
);
