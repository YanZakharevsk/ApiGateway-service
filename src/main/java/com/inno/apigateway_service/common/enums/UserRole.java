package com.inno.apigateway_service.common.enums;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN, USER;

    @Override
    public @Nullable String getAuthority() {

        return "ROLE_" + name();
    }
}