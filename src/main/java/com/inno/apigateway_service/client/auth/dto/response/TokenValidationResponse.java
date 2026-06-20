package com.inno.apigateway_service.client.auth.dto.response;

import com.inno.apigateway_service.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenValidationResponse {
    private Long userId;
    private UserRole userRole;
    private boolean valid;
}
