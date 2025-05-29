package com.basic.miniPjt5.exception.advice;

import com.basic.miniPjt5.DTO.ErrorResponse;
import com.basic.miniPjt5.exception.TMDBApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TMDBApiException.class)
    public ResponseEntity<ErrorResponse> handleTMDBApiException(TMDBApiException e, HttpServletRequest request) {
        logger.error("TMDB API 오류: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("TMDB_API_ERROR")
                .message("TMDB API 호출 중 오류가 발생했습니다")
                .details(e.getMessage())
                .path(request.getRequestURI())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.error("잘못된 인수: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INVALID_ARGUMENT")
                .message("잘못된 요청 매개변수입니다")
                .details(e.getMessage())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        logger.error("잘못된 상태: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INVALID_STATE")
                .message("현재 상태에서 해당 작업을 수행할 수 없습니다")
                .details(e.getMessage())
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.error("유효성 검사 실패: {}", e.getMessage());

        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("입력 데이터 유효성 검사에 실패했습니다")
                .details(details)
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        logger.error("바인딩 오류: {}", e.getMessage());

        String details = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("BINDING_ERROR")
                .message("요청 데이터 바인딩에 실패했습니다")
                .details(details)
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        logger.error("타입 불일치 오류: {}", e.getMessage());

        String details = String.format("매개변수 '%s'의 값 '%s'을(를) %s 타입으로 변환할 수 없습니다",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("TYPE_MISMATCH")
                .message("요청 매개변수 타입이 올바르지 않습니다")
                .details(details)
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("런타임 오류: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("RUNTIME_ERROR")
                .message("서버에서 예상치 못한 오류가 발생했습니다")
                .details(e.getMessage())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        logger.error("예상치 못한 오류: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INTERNAL_ERROR")
                .message("내부 서버 오류가 발생했습니다")
                .details("시스템 관리자에게 문의하세요")
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}