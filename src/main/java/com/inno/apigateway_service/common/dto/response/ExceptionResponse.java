package com.inno.apigateway_service.common.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ExceptionResponse {
    private String errorCode;
    private String message;
    private int status;
    private Instant timestamp;
}
