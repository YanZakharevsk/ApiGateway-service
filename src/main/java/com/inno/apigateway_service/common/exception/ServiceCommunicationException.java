package com.inno.apigateway_service.common.exception;

import org.springframework.http.HttpStatusCode;

public class ServiceCommunicationException extends BaseException {

    public ServiceCommunicationException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
