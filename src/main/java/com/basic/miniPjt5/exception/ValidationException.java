package com.basic.miniPjt5.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ValidationException extends BusinessException {
    private final List<FieldError> fieldErrors;

    public ValidationException(List<FieldError> fieldErrors) {
        super(ErrorCode.VALIDATION_ERROR);
        this.fieldErrors = fieldErrors;
    }

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
    }
}