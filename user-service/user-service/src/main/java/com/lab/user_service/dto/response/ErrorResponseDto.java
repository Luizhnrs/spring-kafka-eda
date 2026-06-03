package com.lab.user_service.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String path;
}