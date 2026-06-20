package com.inno.apigateway_service.security.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ExternalHeaderSanitizerFilter implements WebFilter, Ordered {

    private static final String INTERNAL_KEY_HEADER = "X-Internal-Key";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if(request.getHeaders().containsHeader(INTERNAL_KEY_HEADER)){

            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(httpHeaders -> httpHeaders.remove(INTERNAL_KEY_HEADER))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
