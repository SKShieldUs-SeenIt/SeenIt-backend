package com.basic.miniPjt5.exception.advice;

import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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
     * 검증 예외 처리
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.warn("Validation exception: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getFieldErrors().stream()
                .map(fieldError -> ErrorResponse.FieldError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(e.getErrorCode().getCode())
                        .message(e.getErrorCode().getMessage())
                        .fieldErrors(fieldErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Spring Validation 예외 처리
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
     * 엔티티 없음 예외 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code(e.getErrorCode().getCode())
                        .message(e.getErrorCode().getMessage())
                        .detail(e.getDetail())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 접근 거부 예외 처리
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
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
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