package com.basic.miniPjt5.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String errorCode;
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private String path;
}