package com.basic.miniPjt5.exception.advice;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter @Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
}