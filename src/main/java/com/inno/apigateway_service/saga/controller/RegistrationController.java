package com.inno.apigateway_service.saga.controller;

import com.inno.apigateway_service.client.auth.dto.request.LoginRequest;
import com.inno.apigateway_service.client.user.dto.response.UserResponse;
import com.inno.apigateway_service.saga.dto.request.FullRegisterRequest;
import com.inno.apigateway_service.saga.dto.response.FullRegisterResponse;
import com.inno.apigateway_service.saga.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/auth")
public class RegistrationController {

    private final RegisterService registerService;

    public RegistrationController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FullRegisterResponse> register(@Valid @RequestBody FullRegisterRequest request){
        return registerService.register(request);
    }

    @PostMapping("/login")
    public Mono<FullRegisterResponse> login(@Valid @RequestBody LoginRequest request){
        return registerService.login(request.getLogin(), request.getPassword());
    }
}
