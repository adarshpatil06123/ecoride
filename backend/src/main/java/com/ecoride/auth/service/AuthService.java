package com.ecoride.auth.service;

import com.ecoride.auth.dto.LoginRequest;
import com.ecoride.auth.dto.RegisterRequest;
import com.ecoride.auth.dto.TokenResponse;
import com.ecoride.auth.security.JwtUtil;
import com.ecoride.common.exception.ApiException;
import com.ecoride.user.entity.User;
import com.ecoride.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${ecoride.allowed-email-domain}")
    private String allowedDomain;

    @Transactional
    public TokenResponse register(RegisterRequest req) {
        validateEmailDomain(req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            throw ApiException.conflict("Email already registered");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .department(req.getDepartment())
                .year(req.getYear())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new TokenResponse(token, user.getEmail(), user.getName());
    }

    public TokenResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new TokenResponse(token, user.getEmail(), user.getName());
    }

    private void validateEmailDomain(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        if (!domain.equals(allowedDomain.toLowerCase())) {
            throw ApiException.badRequest("Only @" + allowedDomain + " emails are allowed");
        }
    }
}
