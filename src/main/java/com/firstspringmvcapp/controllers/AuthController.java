package com.firstspringmvcapp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.firstspringmvcapp.dto.AuthenticationDTO;
import com.firstspringmvcapp.dto.PersonDTO;
import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.security.JWTUtil;
import com.firstspringmvcapp.services.AdminService;
import com.firstspringmvcapp.services.PeopleService;
import com.firstspringmvcapp.util.PersonErrorResponse;
import com.firstspringmvcapp.util.PersonNotCreatedException;
import com.firstspringmvcapp.util.PersonValidator;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final PeopleService peopleService;
    private final AdminService adminService;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthController(PersonValidator personValidator, PeopleService peopleService, AdminService adminService,
                          ModelMapper modelMapper, JWTUtil jwtUtil, AuthenticationManager authManager) {
        this.personValidator = personValidator;
        this.peopleService = peopleService;
        this.adminService = adminService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("personDTO") PersonDTO personDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@Valid PersonDTO personDTO,
                                                   BindingResult bRes) {
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bRes);

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
        peopleService.save(person);

        String token = jwtUtil.generateToken(person.getEmail());
//        return Map.of("jwt-token", token);
        return "redirect:/people";
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdminAction();
        return "auth/admin";
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getEmail(),
                        authenticationDTO.getPassword());
        try {
            authManager.authenticate(authInputToken);
        } catch(AuthenticationException e) {
            return Map.of("message", "Incorrect credentials!");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getEmail());
        return Map.of("jwt-token", token);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
