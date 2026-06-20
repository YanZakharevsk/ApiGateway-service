package com.inno.apigateway_service.client.auth.service.impl;

import com.inno.apigateway_service.client.BaseWebClient;
import com.inno.apigateway_service.client.auth.dto.request.LoginRequest;
import com.inno.apigateway_service.client.auth.dto.request.RegisterRequest;
import com.inno.apigateway_service.client.auth.dto.request.ValidateTokenRequest;
import com.inno.apigateway_service.client.auth.dto.response.AuthResponse;
import com.inno.apigateway_service.client.auth.dto.response.RegisterResponse;
import com.inno.apigateway_service.client.auth.dto.response.TokenValidationResponse;
import com.inno.apigateway_service.client.auth.service.AuthServiceClient;
import com.inno.apigateway_service.common.constants.Headers;
import com.inno.apigateway_service.common.constants.PublicPaths;
import com.inno.apigateway_service.common.constants.SecurityPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthServiceClientImpl extends BaseWebClient implements AuthServiceClient {

    private final WebClient authWebClient;
    private final String internalApiKey;

    public AuthServiceClientImpl(ObjectMapper objectMapper, WebClient.Builder webClientBuilder, @Value("${services.auth.url}") String authUrl, @Value("${internal.api-key}") String internalApiKey) {

        super(objectMapper, "auth-service");
        this.internalApiKey = internalApiKey;
        this.authWebClient = webClientBuilder
                .baseUrl(authUrl)
                .build();
    }

    @Override
    public Mono<RegisterResponse> saveCredentials(RegisterRequest request) {
        return authWebClient
                        .post()
                        .uri(SecurityPaths.INTERNAL_CREDENTIALS)
                        .header(Headers.INTERNAL_KEY, internalApiKey)
                        .bodyValue(request)
                        .exchangeToMono(response -> handleResponse(response, RegisterResponse.class))
                        .transform(this:: withConnectionErrorHandling);
    }

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return authWebClient
                .post()
                .uri(PublicPaths.LOGIN)
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .bodyValue(request)
                .exchangeToMono(response -> handleResponse(response, AuthResponse.class))
                .transform(this::withConnectionErrorHandling);
    }

    // dont used because validate token check using jwtProvider in filter
    @Override
    public Mono<TokenValidationResponse> validateToken(ValidateTokenRequest request) {
        return authWebClient
                .post()
                .uri(SecurityPaths.INTERNAL_VALIDATE)
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .bodyValue(request)
                .exchangeToMono(response -> handleResponse(response, TokenValidationResponse.class))
                .transform(this::withConnectionErrorHandling);
    }

    public Mono<Long> getUserId(String login){
        return authWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SecurityPaths.INTERNAL_CREDENTIALS)
                        .queryParam("login", login)
                        .build())
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .exchangeToMono(response -> handleResponse(response, Long.class))
                .transform(this::withConnectionErrorHandling);
    }
}
