package com.ecoride.prediction.controller;

import com.ecoride.common.response.ApiResponse;
import com.ecoride.prediction.dto.PredictionDto;
import com.ecoride.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/suggestions")
    public ApiResponse<List<PredictionDto>> getSuggestions(@AuthenticationPrincipal UserDetails principal) {
        return ApiResponse.ok(predictionService.getSuggestions(principal.getUsername()));
    }
}
