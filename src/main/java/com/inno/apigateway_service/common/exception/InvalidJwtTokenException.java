package com.inno.apigateway_service.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends BaseException {
    public InvalidJwtTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}