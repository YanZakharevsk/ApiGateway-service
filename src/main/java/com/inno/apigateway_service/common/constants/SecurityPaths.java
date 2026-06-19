package com.inno.apigateway_service.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityPaths {

    public String INTERNAL_USERS = "/internal/users";

    public String INTERNAL_USERS_WITH_ID = "/internal/users/";

    public String IS_ACTIVE_USER = "/internal/users/isActive/";

    public String INTERNAL_CREDENTIALS = "/internal/auth/credentials";

    public String INTERNAL_VALIDATE = "/internal/auth/validate";

}
