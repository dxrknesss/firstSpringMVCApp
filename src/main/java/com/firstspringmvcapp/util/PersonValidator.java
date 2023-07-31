package com.firstspringmvcapp.util;

import com.firstspringmvcapp.dto.UserDto;
import com.firstspringmvcapp.models.User;
import com.firstspringmvcapp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserDto user = (UserDto) o;

        if (peopleService.findByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "This email is already taken!");
        }
    }
}
