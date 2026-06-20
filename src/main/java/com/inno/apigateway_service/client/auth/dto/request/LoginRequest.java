package com.inno.apigateway_service.client.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "Username can not be null")
    @NotBlank(message = "Username can not be blank")
    private String login;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be blank")
    private String password;
}
