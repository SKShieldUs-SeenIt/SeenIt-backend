package com.basic.miniPjt5.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    
    private String errorCode;
    private String message;
    private String details;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    private int status;
    
    // 간단한 생성자
    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // 상세 정보 포함 생성자
    public ErrorResponse(String errorCode, String message, String details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // 모든 정보 포함 생성자
    public ErrorResponse(String errorCode, String message, String details, String path, int status) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.path = path;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}