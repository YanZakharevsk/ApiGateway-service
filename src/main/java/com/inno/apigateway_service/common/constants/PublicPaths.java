package com.inno.apigateway_service.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PublicPaths {

    public String REGISTER = "/api/auth/register/**";

    public String LOGIN = "/api/auth/login/**";

    public String REFRESH = "/api/auth/refresh/**";
}
