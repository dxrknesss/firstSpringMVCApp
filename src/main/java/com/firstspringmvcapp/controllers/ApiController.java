package com.firstspringmvcapp.controllers;

import com.firstspringmvcapp.util.PersonNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.firstspringmvcapp.dto.PersonDTO;
import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.security.JWTUtil;
import com.firstspringmvcapp.services.PeopleService;
import com.firstspringmvcapp.util.PersonErrorResponse;
import com.firstspringmvcapp.util.PersonNotCreatedException;
import com.firstspringmvcapp.util.PersonValidator;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;

    @Autowired
    public ApiController(PeopleService peopleService, PersonValidator personValidator, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/people")
    public List<Person> getPeople() {
        return peopleService.findAll();
    }

    @GetMapping("/people/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        return peopleService.findOne(id);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found!", System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/people")
    @ResponseBody
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult) {
        Person finalPerson = convertToPerson(personDTO);
        personValidator.validate(finalPerson, bindingResult);

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatedException(errorMessage.toString());
        }

        peopleService.save(finalPerson);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
