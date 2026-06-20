package com.inno.apigateway_service.saga.service.impl;

import com.inno.apigateway_service.client.auth.dto.request.LoginRequest;
import com.inno.apigateway_service.client.auth.dto.request.RegisterRequest;
import com.inno.apigateway_service.client.auth.service.AuthServiceClient;
import com.inno.apigateway_service.client.user.dto.request.CreateUserRequest;
import com.inno.apigateway_service.client.user.dto.response.UserResponse;
import com.inno.apigateway_service.client.user.service.UserServiceClient;
import com.inno.apigateway_service.common.exception.RollbackFailedException;
import com.inno.apigateway_service.common.exception.ServiceCommunicationException;
import com.inno.apigateway_service.common.exception.UserDeactivateException;
import com.inno.apigateway_service.saga.dto.request.FullRegisterRequest;
import com.inno.apigateway_service.saga.dto.response.FullRegisterResponse;
import com.inno.apigateway_service.saga.mapper.RegisterMapper;
import com.inno.apigateway_service.saga.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserServiceClient userServiceClient;

    private final AuthServiceClient authServiceClient;

    private final RegisterMapper mapper;


    public RegisterServiceImpl(UserServiceClient userServiceClient, AuthServiceClient authServiceClient, RegisterMapper mapper) {
        this.userServiceClient = userServiceClient;
        this.authServiceClient = authServiceClient;
        this.mapper = mapper;
    }

    @Override
    public Mono<FullRegisterResponse> register(FullRegisterRequest request) {

        CreateUserRequest createUserRequest = mapper.toCreateUserRequest(request);

        return userServiceClient.createUser(createUserRequest)
                .onErrorMap(error ->{
                    if(error instanceof ServiceCommunicationException){
                        return error;
                    }
                    return new ServiceCommunicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed");
                }).flatMap(user -> createCredentials(request, user));
    }

    private Mono<FullRegisterResponse> createCredentials(FullRegisterRequest request, UserResponse user) {

        RegisterRequest registerRequest = mapper.toRegisterRequest(request, user.getId());

        return authServiceClient.saveCredentials(registerRequest)
                .onErrorMap(error -> {
                        if (error instanceof ServiceCommunicationException) {
                            return error;
                        }
                        return new ServiceCommunicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user credentials");
                })
                .flatMap(credentials -> login(request.getLogin(), request.getPassword()))
                .onErrorResume(exception -> rollbackUser(user.getId()).then(Mono.error(exception)));
    }

    private Mono<Void> rollbackUser(Long userId) {
        return userServiceClient.deleteUser(userId)
                .onErrorMap(error -> new RollbackFailedException("Failed to rollback created user with id: " + userId, error));
    }

    public Mono<FullRegisterResponse> login(String login, String password) {

        LoginRequest loginRequest = new LoginRequest(login, password);

        return authServiceClient.getUserId(login)
                .onErrorMap(error -> {
                    if (error instanceof ServiceCommunicationException) {
                        return error;
                    }
                    return new ServiceCommunicationException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to get user id"
                    );
                })

                .flatMap(userId ->
                        userServiceClient.isActiveUser(userId)
                                .onErrorMap(error -> {
                                    if (error instanceof ServiceCommunicationException) {
                                        return error;
                                    }
                                    return new ServiceCommunicationException(
                                            HttpStatus.INTERNAL_SERVER_ERROR,
                                            "Failed to check user activation"
                                    );
                                })

                                .flatMap(isActive -> {
                                    if (!isActive) {
                                        return Mono.error(new UserDeactivateException("User is banned"));
                                    }

                                    return authServiceClient.login(loginRequest)
                                            .onErrorMap(error -> {
                                                if (error instanceof ServiceCommunicationException) {
                                                    return error;
                                                }
                                                return new ServiceCommunicationException(
                                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to login"
                                                );
                                            })
                                            .map(mapper::toFullRegisterResponse);
                                })
                );
    }

}
