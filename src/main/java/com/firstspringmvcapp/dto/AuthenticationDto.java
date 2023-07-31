package com.firstspringmvcapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDto {
    @NotEmpty(message = "Email should not be empty!")
    @Email(message = "You're not even trying... Enter valid email!")
    private String email;

    @NotEmpty(message = "Password should not be empty!")
    private String password;
}
