package com.inno.apigateway_service.saga.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FullRegisterRequest {

    @NotBlank(message = "Name field can not be empty")
    @Size(max = 100, message = "Name field must be shorter than 100 symbols")
    private String name;

    @NotBlank(message = "Surname field can not be empty")
    @Size(max = 100, message = "Surname field must be shorter than 100 symbols")
    private String surname;

    @NotNull(message = "Date born can not be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private @Past(message = "The Birth date must be in the past") LocalDate birthDate;

    @Email(message = "The format email is incorrect")
    @NotBlank(message = "Email field can not be empty")
    @Size(min = 6, max = 80, message = "The email length must be between 6 and 80")
    private String email;

    @NotNull(message = "Username can not be null")
    @NotBlank(message = "Username can not be blank")
    @Size(min = 3, max = 255, message = "Minimum username length: 3 characters")
    private String login;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be blank")
    @Size(min = 8, message = "Minimum password length: 8 characters")
    private String password;
}
