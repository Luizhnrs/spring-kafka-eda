package com.lab.user_service.controller;

import com.lab.user_service.dto.request.LoginRequestDto;
import com.lab.user_service.dto.request.RegisterRequestDto;
import com.lab.user_service.dto.response.LoginResponseDto;
import com.lab.user_service.dto.response.UserResponseDto;
import com.lab.user_service.security.JwtUtils;
import com.lab.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String email = authentication.getName();
        String token = jwtUtils.generateToken(email);
        UserResponseDto user = userService.findByEmail(email);

        return ResponseEntity.ok(LoginResponseDto.of(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserResponseDto user = userService.register(request);
        String token = jwtUtils.generateToken(user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoginResponseDto.of(token, user));
    }
}