package com.ecoride.user.controller;

import com.ecoride.common.response.ApiResponse;
import com.ecoride.user.dto.UserProfileDto;
import com.ecoride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserProfileDto> getMe(@AuthenticationPrincipal UserDetails principal) {
        return ApiResponse.ok(userService.getByEmail(principal.getUsername()));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfileDto> getById(@PathVariable UUID id) {
        return ApiResponse.ok(userService.getById(id));
    }
}
