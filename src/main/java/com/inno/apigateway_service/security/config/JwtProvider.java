package com.inno.apigateway_service.security.config;

import com.inno.apigateway_service.common.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class JwtProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    private SecretKey signingKey;

    @PostConstruct
    protected void init(){
        try{
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
            signingKey = Keys.hmacShaKeyFor(keyBytes);
        }catch (NoSuchAlgorithmException ex){
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    public Long getUserIdFromToken(String token){
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token){
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException ex){
            throw new InvalidJwtTokenException("Expired or invalid JWT token");
        }
    }
}
