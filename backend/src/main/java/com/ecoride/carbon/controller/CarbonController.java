package com.ecoride.carbon.controller;

import com.ecoride.carbon.dto.CampusSummaryDto;
import com.ecoride.carbon.dto.WalletDto;
import com.ecoride.carbon.service.CarbonService;
import com.ecoride.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class CarbonController {

    private final CarbonService carbonService;

    @GetMapping
    public ApiResponse<WalletDto> getWallet(@AuthenticationPrincipal UserDetails principal) {
        return ApiResponse.ok(carbonService.getWallet(principal.getUsername()));
    }

    @GetMapping("/campus-summary")
    public ApiResponse<CampusSummaryDto> getCampusSummary() {
        return ApiResponse.ok(carbonService.getCampusSummary());
    }
}
