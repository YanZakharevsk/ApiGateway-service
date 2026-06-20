package com.inno.apigateway_service.client.user.service.impl;

import com.inno.apigateway_service.client.BaseWebClient;
import com.inno.apigateway_service.client.user.dto.request.CreateUserRequest;
import com.inno.apigateway_service.client.user.dto.response.UserResponse;
import com.inno.apigateway_service.client.user.service.UserServiceClient;
import com.inno.apigateway_service.common.constants.Headers;
import com.inno.apigateway_service.common.constants.PublicPaths;
import com.inno.apigateway_service.common.constants.SecurityPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserServiceClientImpl extends BaseWebClient implements UserServiceClient  {

    private final WebClient userWebClient;
    private final String internalApiKey;

    public UserServiceClientImpl(ObjectMapper objectMapper, WebClient.Builder webClientBuilder, @Value("${services.user.url}") String userUrl, @Value("${internal.api-key}") String internalApiKey) {
        super(objectMapper, "user-service");
        this.internalApiKey = internalApiKey;
        this.userWebClient = webClientBuilder
                .baseUrl(userUrl)
                .build();
    }

    @Override
    public Mono<UserResponse> createUser(CreateUserRequest request) {
        return userWebClient
                .post()
                .uri(SecurityPaths.INTERNAL_USERS)
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .bodyValue(request)
                .exchangeToMono(response -> handleResponse(response, UserResponse.class))
                .transform(this::withConnectionErrorHandling);
    }

    @Override
    public Mono<Void> deleteUser(Long userId) {
        return userWebClient
                .delete()
                .uri(SecurityPaths.INTERNAL_USERS_WITH_ID + userId)
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .exchangeToMono(response -> handleVoidResponse(response))
                .transform(this::withConnectionErrorHandling);
    }

    @Override
    public Mono<Boolean> isActiveUser(Long userId) {
        return userWebClient
                .get()
                .uri(SecurityPaths.IS_ACTIVE_USER + userId)
                .header(Headers.INTERNAL_KEY, internalApiKey)
                .retrieve()
                .bodyToMono(Boolean.class)
                .map(isActiveUser -> isActiveUser ? true : false)
                .transform(this::withConnectionErrorHandling);
    }


}
