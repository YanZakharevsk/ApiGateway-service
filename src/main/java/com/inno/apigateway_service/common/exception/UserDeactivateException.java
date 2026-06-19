package com.inno.apigateway_service.common.exception;

import org.springframework.http.HttpStatus;

public class UserDeactivateException extends BaseException {
    public UserDeactivateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
