package com.ecoride.trust.controller;

import com.ecoride.common.response.ApiResponse;
import com.ecoride.trust.dto.TrustProfileDto;
import com.ecoride.trust.service.TrustService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trust")
@RequiredArgsConstructor
public class TrustController {

    private final TrustService trustService;

    @GetMapping("/{userId}")
    public ApiResponse<TrustProfileDto> getTrustProfile(@PathVariable UUID userId) {
        return ApiResponse.ok(trustService.getTrustProfile(userId));
    }
}
