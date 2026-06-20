package com.inno.apigateway_service.common.exception;

import org.springframework.http.HttpStatus;

public class RollbackFailedException extends BaseException {
    public RollbackFailedException(String message, Throwable throwable) {
        super(message, throwable, HttpStatus.BAD_GATEWAY);
    }
}
