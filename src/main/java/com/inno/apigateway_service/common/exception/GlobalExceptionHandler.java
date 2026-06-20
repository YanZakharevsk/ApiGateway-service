package com.inno.apigateway_service.common.exception;

import com.inno.apigateway_service.common.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<ExceptionResponse> handleServiceCommunicationException(ServiceCommunicationException exception){

        String cleanMessage = finalClean(exception.getMessage());
        return createExceptionResponse(exception, cleanMessage);
    }

    @ExceptionHandler(RollbackFailedException.class)
    public ResponseEntity<ExceptionResponse> handleRollbackFailedException(RollbackFailedException exception){
        return createExceptionResponse(exception);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJwtTokenException(InvalidJwtTokenException ex){
        return createExceptionResponse(ex);
    }

    @ExceptionHandler(UserDeactivateException.class)
    public ResponseEntity<ExceptionResponse> handleUserDeactivateException(UserDeactivateException ex){
        return createExceptionResponse(ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(){
        ExceptionResponse response = new ExceptionResponse(
                "AUTHENTICATION ERROR",
                "Authentication required",
                HttpStatus.UNAUTHORIZED.value(),
                Instant.now()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(){
        ExceptionResponse response = new ExceptionResponse(
                "AUTHORIZATION ERROR",
                "Access Denied",
                HttpStatus.FORBIDDEN.value(),
                Instant.now()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(WebExchangeBindException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("VALIDATION_ERROR");
        response.setMessage(message);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(Instant.now());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String finalClean(String message) {
        if (message == null) {
            return "Unknown service error";
        }

        try {
            if (message.trim().startsWith("{")) {
                JsonNode jsonNode = objectMapper.readTree(message);
                if (jsonNode.has("message")) {
                    String innerMessage = jsonNode.get("message").asText();
                    return finalClean(innerMessage);
                }
                if (jsonNode.has("error")) {
                    return jsonNode.get("error").asText();
                }
            }
        } catch (Exception ignored) { return ignored.getMessage();}
        return message;
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponse(BaseException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode(HttpStatus.valueOf(ex.getStatus().value()).getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setStatus(ex.getStatus().value());
        response.setTimestamp(Instant.now());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponse(BaseException ex, String message) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode(HttpStatus.valueOf(ex.getStatus().value()).getReasonPhrase());
        response.setMessage(message);
        response.setStatus(ex.getStatus().value());
        response.setTimestamp(Instant.now());
        return new ResponseEntity<>(response, ex.getStatus());
    }
}
