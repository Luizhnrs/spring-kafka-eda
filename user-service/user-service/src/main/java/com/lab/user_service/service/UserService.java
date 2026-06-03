package com.lab.user_service.service;

import com.lab.user_service.dto.request.RegisterRequestDto;
import com.lab.user_service.dto.response.UserResponseDto;
import com.lab.user_service.entity.User;
import com.lab.user_service.exception.DuplicateResourceException;
import com.lab.user_service.exception.ResourceNotFoundException;
import com.lab.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto register(RegisterRequestDto requestDto) {
        log.info("Registering user with email: {}", requestDto.getEmail());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", requestDto.getEmail());
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .deliveryAddress(requestDto.getDeliveryAddress())
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered with id: {}", user.getId());
        return toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toResponseDto(user);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .deliveryAddress(user.getDeliveryAddress())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}