package com.ecoride.ride.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateRideRequest {
    @NotBlank
    private String pickupZone;

    @NotNull @Future
    private Instant departureTime;

    @Min(1) @Max(8)
    private int availableSeats;

    private boolean subscription;
}
