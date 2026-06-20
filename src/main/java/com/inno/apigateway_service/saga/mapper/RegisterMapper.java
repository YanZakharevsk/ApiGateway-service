package com.inno.apigateway_service.saga.mapper;

import com.inno.apigateway_service.client.auth.dto.request.RegisterRequest;
import com.inno.apigateway_service.client.auth.dto.response.AuthResponse;
import com.inno.apigateway_service.client.user.dto.request.CreateUserRequest;
import com.inno.apigateway_service.saga.dto.request.FullRegisterRequest;
import com.inno.apigateway_service.saga.dto.response.FullRegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterMapper {

    CreateUserRequest toCreateUserRequest(FullRegisterRequest request);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userRole", constant = "USER")
    RegisterRequest toRegisterRequest(FullRegisterRequest request, Long userId);

    FullRegisterResponse toFullRegisterResponse(AuthResponse token);
}
