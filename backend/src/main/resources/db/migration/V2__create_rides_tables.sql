-- V2: Create rides and ride_participants tables
CREATE TABLE rides (
    id               VARCHAR(36)   PRIMARY KEY DEFAULT (UUID()),
    driver_id        VARCHAR(36)   NOT NULL,
    pickup_zone      VARCHAR(50)   NOT NULL,
    departure_time   DATETIME      NOT NULL,
    available_seats  INT           NOT NULL CHECK (available_seats >= 0),
    status           ENUM('OPEN','FULL','COMPLETED','CANCELLED') NOT NULL DEFAULT 'OPEN',
    is_subscription  BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rides_driver FOREIGN KEY (driver_id) REFERENCES users(id)
);

-- Composite index for matching engine queries
CREATE INDEX idx_rides_search ON rides(pickup_zone, departure_time, status);
CREATE INDEX idx_rides_driver ON rides(driver_id);

CREATE TABLE ride_participants (
    id          VARCHAR(36)    PRIMARY KEY DEFAULT (UUID()),
    ride_id     VARCHAR(36)    NOT NULL,
    user_id     VARCHAR(36)    NOT NULL,
    status      ENUM('REQUESTED','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'REQUESTED',
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_ride_user (ride_id, user_id),
    CONSTRAINT fk_rp_ride FOREIGN KEY (ride_id) REFERENCES rides(id),
    CONSTRAINT fk_rp_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_rp_ride ON ride_participants(ride_id);
CREATE INDEX idx_rp_user ON ride_participants(user_id);
