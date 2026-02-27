package com.ecoride.subscription.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {
    @NotBlank
    private String pickupZone;

    /** "HH:mm" format */
    @NotBlank @Pattern(regexp = "^\\d{2}:\\d{2}$")
    private String departureTime;

    /** 0=Sun, 6=Sat */
    @Min(0) @Max(6)
    private int dayOfWeek;
}
