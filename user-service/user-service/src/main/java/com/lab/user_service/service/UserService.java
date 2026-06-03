package com.lab.user_service.service;

import com.lab.user_service.dto.request.UserRequestDto;
import com.lab.user_service.dto.request.UserUpdateDto;
import com.lab.user_service.dto.response.UserResponseDto;
import com.lab.user_service.entity.User;
import com.lab.user_service.exception.DuplicateResourceException;
import com.lab.user_service.exception.ResourceNotFoundException;
import com.lab.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        log.info("Creating user with email: {}", requestDto.getEmail());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", requestDto.getEmail());
        }

        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .phone(requestDto.getPhone())
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User created with id: {}", user.getId());
        return toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toResponseDto(user);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            if (!updateDto.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(updateDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPassword() != null) {
            user.setPassword(updateDto.getPassword());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }

        user = userRepository.save(user);
        log.info("User updated with id: {}", user.getId());
        return toResponseDto(user);
    }

    @Transactional
    public void deactivate(Long id) {
        log.info("Deactivating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated with id: {}", id);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}