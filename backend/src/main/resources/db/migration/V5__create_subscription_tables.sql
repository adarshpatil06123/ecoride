-- V5: Create subscription_pools and subscription_members tables
CREATE TABLE subscription_pools (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT (UUID()),
    pickup_zone     VARCHAR(50)  NOT NULL,
    departure_time  TIME         NOT NULL,
    day_of_week     INT          NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    created_by      VARCHAR(36)  NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sp_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_pool_zone_day ON subscription_pools(pickup_zone, day_of_week);

CREATE TABLE subscription_members (
    pool_id  VARCHAR(36) NOT NULL,
    user_id  VARCHAR(36) NOT NULL,
    PRIMARY KEY (pool_id, user_id),
    CONSTRAINT fk_sm_pool FOREIGN KEY (pool_id) REFERENCES subscription_pools(id),
    CONSTRAINT fk_sm_user FOREIGN KEY (user_id) REFERENCES users(id)
);
