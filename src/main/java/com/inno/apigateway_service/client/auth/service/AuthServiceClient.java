package com.inno.apigateway_service.client.auth.service;

import com.inno.apigateway_service.client.auth.dto.request.LoginRequest;
import com.inno.apigateway_service.client.auth.dto.request.RefreshRequest;
import com.inno.apigateway_service.client.auth.dto.request.RegisterRequest;
import com.inno.apigateway_service.client.auth.dto.request.ValidateTokenRequest;
import com.inno.apigateway_service.client.auth.dto.response.AuthResponse;
import com.inno.apigateway_service.client.auth.dto.response.RegisterResponse;
import com.inno.apigateway_service.client.auth.dto.response.TokenValidationResponse;
import reactor.core.publisher.Mono;

public interface AuthServiceClient {

    Mono<RegisterResponse> saveCredentials(RegisterRequest request);

    Mono<AuthResponse> login(LoginRequest request);

    Mono<TokenValidationResponse> validateToken(ValidateTokenRequest request);
    Mono<Long> getUserId(String login);
}
