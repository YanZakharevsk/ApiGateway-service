package com.inno.apigateway_service.client;

import com.inno.apigateway_service.common.exception.ServiceCommunicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeoutException;


public abstract class BaseWebClient {

    private final ObjectMapper objectMapper;
    private final String serviceName;

    public BaseWebClient(ObjectMapper objectMapper, String serviceName) {
        this.objectMapper = objectMapper;
        this.serviceName = serviceName;
    }

    protected <T> Mono<T> handleResponse(ClientResponse response, Class<T> bodyType){
        if(response.statusCode().isError()){
            return handleHttpError(response);
        }
        return response.bodyToMono(bodyType);
    }

    protected Mono<Void> handleVoidResponse(ClientResponse response){
        if(response.statusCode().isError()){
            return handleHttpError(response);
        }
        return Mono.empty();
    }

    protected <T> Mono<T> withConnectionErrorHandling(Mono<T> mono){
        return mono.onErrorMap(this::mapConnectionError);
    }

    private Throwable mapConnectionError(Throwable throwable) {
        if(throwable instanceof ServiceCommunicationException){
            return throwable;
        }

        if(isConnectionError(throwable)){
            String message = getServiceUnavailableMessage();
            return new ServiceCommunicationException(HttpStatus.SERVICE_UNAVAILABLE, message);
        }

        return throwable;
    }

    private boolean isConnectionError(Throwable throwable) {
        Throwable cause = throwable;

        while(cause != null){
            if(cause instanceof ConnectException ||
            cause instanceof TimeoutException ||
            cause instanceof ClosedChannelException){
                return true;
            }

            if(cause.getMessage() != null){
                String msg = cause.getMessage().toLowerCase();
                if(msg.contains("connection refused") ||
                msg.contains("connection reset") ||
                msg.contains("no route to host") ||
                msg.contains("host is down") ||
                msg.contains("connection timed out")){
                    return true;
                }
            }

            cause = cause.getCause();
        }
        return false;
    }

    private String getServiceUnavailableMessage() {
        if(serviceName == null){
            return "Internal server error";
        }

        return switch (serviceName.toLowerCase()){
            case "auth-service" -> "Authentication service is unavailable";
            case "user-service" -> "User service is unavailable";
            default -> serviceName + " is temporarily unavailable";
        };

    }


    private <T> Mono<T> handleHttpError(ClientResponse response) {

        HttpStatusCode statusCode = response.statusCode();

        return response.bodyToMono(String.class)
                .defaultIfEmpty("Unknown service error")
                .flatMap(errorBody -> {
                    String errorMessage = parseErrorMessage(errorBody);
                    return Mono.error(new ServiceCommunicationException(statusCode, errorMessage));
                });
    }

    private String parseErrorMessage(String errorBody) {
        try{
            JsonNode jsonNode = objectMapper.readTree(errorBody);

            if(jsonNode.has("message")){
                String message = jsonNode.get("message").asText();

                if(message != null && message.trim().startsWith("{")){
                    return parseErrorMessage(message);
                }
                return message;
            }

            if(jsonNode.has("errorCode")){
                return jsonNode.get("errorCode").asText();
            }

            if(jsonNode.has("errors") && jsonNode.get("errors").isArray()){
                StringBuilder sb = new StringBuilder();
                jsonNode.get("errors").forEach(error ->{
                    if(error.has("field") && error.has("message")){
                        if(!sb.isEmpty()) sb.append("; ");
                        sb.append(error.get("field").asText())
                                .append(": ")
                                .append(error.get("message").asText());
                    }
                });
                if(!sb.isEmpty()) return sb.toString();
            }
            return errorBody;
        }catch (Exception e){
            return errorBody;
        }
    }

}
