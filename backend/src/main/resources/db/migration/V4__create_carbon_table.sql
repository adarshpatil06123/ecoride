-- V4: Create carbon_transactions table
CREATE TABLE carbon_transactions (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT (UUID()),
    user_id             VARCHAR(36)  NOT NULL,
    ride_id             VARCHAR(36)  NOT NULL,
    carbon_saved_grams  INT          NOT NULL CHECK (carbon_saved_grams >= 0),
    credits_earned      INT          NOT NULL CHECK (credits_earned >= 0),
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_carbon_user_ride (user_id, ride_id),
    CONSTRAINT fk_carbon_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_carbon_ride FOREIGN KEY (ride_id) REFERENCES rides(id)
);

CREATE INDEX idx_carbon_user ON carbon_transactions(user_id);
CREATE INDEX idx_carbon_ride ON carbon_transactions(ride_id);
