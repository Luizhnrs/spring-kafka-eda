package com.lab.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {

    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 6, max = 200, message = "Password must be between 6 and 200 characters")
    private String password;

    @Size(max = 20, message = "Phone must have at most 20 characters")
    private String phone;
}