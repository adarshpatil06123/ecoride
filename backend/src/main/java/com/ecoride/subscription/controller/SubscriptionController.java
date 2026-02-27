package com.ecoride.subscription.controller;

import com.ecoride.common.response.ApiResponse;
import com.ecoride.subscription.dto.CreateSubscriptionRequest;
import com.ecoride.subscription.dto.SubscriptionDto;
import com.ecoride.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /** POST /subscription — create a new recurring ride pool */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionDto> create(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateSubscriptionRequest req) {
        return ApiResponse.ok("Subscription pool created",
                subscriptionService.createPool(principal.getUsername(), req));
    }

    /** POST /subscription/{id}/join — join an existing pool */
    @PostMapping("/{id}/join")
    public ApiResponse<SubscriptionDto> join(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable UUID id) {
        return ApiResponse.ok("Joined subscription pool",
                subscriptionService.joinPool(principal.getUsername(), id));
    }

    /** GET /subscription/my — get all pools the current user is a member of */
    @GetMapping("/my")
    public ApiResponse<List<SubscriptionDto>> myPools(
            @AuthenticationPrincipal UserDetails principal) {
        return ApiResponse.ok(subscriptionService.getMyPools(principal.getUsername()));
    }
}
