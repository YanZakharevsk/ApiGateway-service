package com.inno.apigateway_service.saga.service;

import com.inno.apigateway_service.client.auth.dto.request.LoginRequest;
import com.inno.apigateway_service.client.user.dto.response.UserResponse;
import com.inno.apigateway_service.saga.dto.request.FullRegisterRequest;
import com.inno.apigateway_service.saga.dto.response.FullRegisterResponse;
import reactor.core.publisher.Mono;

public interface RegisterService {

    Mono<FullRegisterResponse> register(FullRegisterRequest request);
    Mono<FullRegisterResponse> login(String login, String password);
}
