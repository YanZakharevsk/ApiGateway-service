package com.inno.apigateway_service.security.filter;

import com.inno.apigateway_service.common.constants.Headers;
import com.inno.apigateway_service.common.constants.PublicPaths;
import com.inno.apigateway_service.security.config.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter {

    private final JwtProvider jwtProvider;
    private final String internalApiKey;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    private final List<String> publicPaths = List.of(
            PublicPaths.REGISTER,
            PublicPaths.LOGIN,
            PublicPaths.REFRESH
    );

    public JwtAuthFilter(JwtProvider jwtProvider, @Value("${internal.api-key}") String internalApiKey) {
        this.jwtProvider = jwtProvider;
        this.internalApiKey = internalApiKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if(isPublicPath(path)){
            ServerHttpRequest mutatedRequest = removeUnexpectedHeaders(exchange);
            ServerHttpRequest finalRequest =  mutatedRequest.mutate()
                    .header(Headers.INTERNAL_KEY, internalApiKey)
                    .build();
            return chain.filter(exchange.mutate().request(finalRequest).build());
        }

        return resolveToken(exchange)
                .flatMap(token ->{
                    try{
                        if(!jwtProvider.validateToken(token)){
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }

                        String userId = String.valueOf(jwtProvider.getUserIdFromToken(token));
                        String userRole = jwtProvider.getRoleFromToken(token);

                        ServerHttpRequest mutatedRequest = removeUnexpectedHeaders(exchange);

                        ServerHttpRequest finalRequest =  mutatedRequest.mutate()
                                .header(Headers.USER_ID, userId)
                                .header(Headers.USER_ROLE, userRole)
                                .header(Headers.INTERNAL_KEY, internalApiKey)
                                .build();

                        return chain.filter(exchange.mutate().request( finalRequest).build());
                    }catch (Exception e){
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }
                });
    }

    private boolean isPublicPath(String path) {

        return publicPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private ServerHttpRequest removeUnexpectedHeaders(ServerWebExchange exchange){
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove(Headers.USER_ID);
                    httpHeaders.remove(Headers.USER_ROLE);
                })
                .build();
        return mutatedRequest;
    }

    public Mono<String> resolveToken(ServerWebExchange exchange){
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return Mono.just(bearerToken.substring(7));
        }
        return onError(exchange, HttpStatus.UNAUTHORIZED)
                .then(Mono.empty());
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }


}
