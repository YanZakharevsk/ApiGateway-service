package com.inno.apigateway_service.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor(force = true)
@Data
public class BaseException extends RuntimeException{

    private HttpStatusCode status;

    public BaseException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }

    public BaseException(String message, Throwable throwable, HttpStatusCode status){
        super(message, throwable);
        this.status = status;
    }
}
