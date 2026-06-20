package com.inno.apigateway_service.client.auth.dto.response;

import com.inno.apigateway_service.common.enums.UserRole;
import lombok.Data;

@Data
public class RegisterResponse {
    private Long userId;
    private String login;
    private UserRole userRole;
}
