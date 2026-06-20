package com.inno.apigateway_service.saga.dto.response;

import lombok.Data;

@Data
public class FullRegisterResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
