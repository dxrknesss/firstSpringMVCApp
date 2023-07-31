package com.firstspringmvcapp.controllers;

import com.firstspringmvcapp.dto.AuthenticationDto;
import com.firstspringmvcapp.dto.UserDto;
import com.firstspringmvcapp.models.User;
import com.firstspringmvcapp.services.PeopleService;
import com.firstspringmvcapp.util.PersonNotCreatedException;
import com.firstspringmvcapp.util.PersonValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final PeopleService peopleService;

    @Autowired
    public AuthController(PersonValidator personValidator, PeopleService peopleService) {
        this.personValidator = personValidator;
        this.peopleService = peopleService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("userDto") UserDto userDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@Valid UserDto userDTO,
                                                   BindingResult bRes) {
        User user = peopleService.userDtoToUser(userDTO);
        personValidator.validate(user, bRes);

        if (bRes.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bRes.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatedException(errorMessage.toString());
        }
        peopleService.save(user);

        return "redirect:/people";
    }
}
