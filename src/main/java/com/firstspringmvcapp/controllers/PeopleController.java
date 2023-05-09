package com.firstspringmvcapp.controllers;

import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.services.PeopleService;
import com.firstspringmvcapp.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @ModelAttribute("friend") Person friend) {
        Person person = peopleService.findOne(id);

        model.addAttribute("person", person);
        model.addAttribute("friends", peopleService.findFriends(id));
        model.addAttribute("availableFriends", peopleService.findAvailableFriends(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String createPerson(@ModelAttribute("person") @Valid Person person,
                               BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        peopleService.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String editPerson(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOne(id));

        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String updatePerson(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                               @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }

    @PatchMapping("/{id}/addFriend")
    public String addFriend(@PathVariable("id") int personId, @ModelAttribute("friend") Person friend) {
        peopleService.addFriendById(personId, friend.getId());
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}/addFriend")
    public String deleteFriend(@PathVariable("id") int personId, @RequestParam("friendId") int friendId) {
        peopleService.removeFriendById(personId, friendId);
        return "redirect:/people/{id}";
    }
}