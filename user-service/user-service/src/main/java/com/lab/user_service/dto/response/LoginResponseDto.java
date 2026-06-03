package com.lab.user_service.dto.response;

import com.lab.user_service.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String token;
    private String type;
    private Long id;
    private String name;
    private String email;
    private Role role;

    public static LoginResponseDto of(String token, UserResponseDto user) {
        return LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}