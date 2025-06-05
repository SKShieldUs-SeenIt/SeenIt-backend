package com.basic.miniPjt5.exception.advice;

import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@RestControllerAdvice(basePackages = "com.basic.miniPjt5.controller")
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 (기존 코드)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(e.getErrorCode().getCode())
                        .message(e.getErrorCode().getMessage())
                        .detail(e.getDetail())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    /**
     * Spring Validation 예외 처리 (기존 코드)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation exception: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorResponse.FieldError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(ErrorCode.VALIDATION_ERROR.getCode())
                        .message(ErrorCode.VALIDATION_ERROR.getMessage())
                        .fieldErrors(fieldErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Bind 예외 처리 (추가)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.warn("Bind exception: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorResponse.FieldError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(ErrorCode.VALIDATION_ERROR.getCode())
                        .message(ErrorCode.VALIDATION_ERROR.getMessage())
                        .fieldErrors(fieldErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리 (추가)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("BAD_REQUEST")
                        .message(e.getMessage())
                        .detail("잘못된 요청입니다.")
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * IllegalStateException 처리 (추가)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("CONFLICT")
                        .message(e.getMessage())
                        .detail("요청을 처리할 수 없는 상태입니다.")
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * 접근 거부 예외 처리 (기존 코드)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(ErrorCode.AUTH_ACCESS_DENIED.getCode())
                        .message(ErrorCode.AUTH_ACCESS_DENIED.getMessage())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 일반 예외 처리 - Swagger 경로 제외 (수정됨)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, HttpServletRequest request) {
        // Swagger 관련 경로는 예외 처리에서 완전히 제외
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/swagger-ui") ||
                requestURI.contains("/v3/api-docs") ||
                requestURI.contains("/swagger-resources") ||
                requestURI.contains("/webjars")) {

            log.debug("Swagger related exception - letting Spring handle it: {}", e.getMessage());
            return null; // null을 리턴하면 다음 ExceptionResolver가 처리
        }

        log.error("Unexpected error: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                        .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}