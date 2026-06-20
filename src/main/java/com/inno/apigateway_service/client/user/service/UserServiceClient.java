package com.inno.apigateway_service.client.user.service;

import com.inno.apigateway_service.client.user.dto.request.CreateUserRequest;
import com.inno.apigateway_service.client.user.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface UserServiceClient {

    Mono<UserResponse> createUser(CreateUserRequest request);

    Mono<Void> deleteUser(Long userId);

    Mono<Boolean> isActiveUser(Long userId);
}
