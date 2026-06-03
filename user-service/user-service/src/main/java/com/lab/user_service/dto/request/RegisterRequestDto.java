package com.lab.user_service.dto.request;

import com.lab.user_service.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 200, message = "Password must be between 6 and 200 characters")
    private String password;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    @NotBlank(message = "Phone is mandatory")
    @Size(max = 20, message = "Phone must have at most 20 characters")
    private String phone;

    @NotBlank(message = "Delivery address is mandatory")
    @Size(max = 300, message = "Delivery address must have at most 300 characters")
    private String deliveryAddress;

    @NotNull(message = "Role is mandatory")
    private Role role;
}