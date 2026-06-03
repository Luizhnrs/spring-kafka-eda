package com.lab.user_service.dto.response;

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

    public static LoginResponseDto of(String token, UserResponseDto user) {
        return LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}