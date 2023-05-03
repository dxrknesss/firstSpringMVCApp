package com.firstspringmvcapp.models;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = false)
public class Person {
    private int id;

    @Min(value = 1, message = "Age should be greater than 0")
    @Max(value = 110, message = "Aren't you a bit too old to use the internet?")
    private int age;

    @NotEmpty(message = "Name should not be empty!")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 letters!")
    private String name;

    @NotEmpty(message = "Email should not be empty!")
    @Email(message = "You're not even trying... Enter valid email!")
    private String email;
}
