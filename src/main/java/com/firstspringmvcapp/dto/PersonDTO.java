package com.firstspringmvcapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDTO {
    @Min(value = 1, message = "Age should be greater than 0")
    @Max(value = 110, message = "Aren't you a bit too old to use the internet?")
    private int age;

    @NotEmpty(message = "Name should not be empty!")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 letters!")
    private String name;

    @NotEmpty(message = "Email should not be empty!")
    @Email(message = "You're not even trying... Enter valid email!")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @NotEmpty(message = "Password should not be empty!")
    private String password;
}
