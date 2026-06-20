package com.inno.apigateway_service.client.auth.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
