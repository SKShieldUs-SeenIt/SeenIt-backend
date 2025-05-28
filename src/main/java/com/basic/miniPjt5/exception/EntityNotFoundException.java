package com.basic.miniPjt5.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String entityName, Object identifier) {
        super(ErrorCode.valueOf(entityName.toUpperCase() + "_NOT_FOUND"), 
              String.format("%s인 %s를 찾을 수 없습니다. ", identifier, entityName));
    }
}