package com.firstspringmvcapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = null;
        try {
            person = (Person) o;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peopleService.findByEmail(person.getEmail()) != null) {
            System.out.println("Error is in the email!");
            errors.rejectValue("email", "", "This email is already taken!");
        }
    }
}
