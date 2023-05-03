package com.firstspringmvcapp.controllers;

import com.firstspringmvcapp.dao.IndexState;
import com.firstspringmvcapp.dao.PersonDAO;
import com.firstspringmvcapp.models.Person;
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
    private final PersonDAO personDAO;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PersonDAO personDAO, PersonValidator personValidator) {
        this.personDAO = personDAO;
        this.personValidator = personValidator;
    }


    @GetMapping()
    public String index(Model model) {
        // receive all people from DAO and send them to view
        model.addAttribute("people", personDAO.index(IndexState.WITH, 0));
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @ModelAttribute("friend") Person friend) {
        // receive one person by id and send it to view
        model.addAttribute("person", personDAO.show(id));
        model.addAttribute("friends", personDAO.getFriends(id));
        model.addAttribute("availableFriends", personDAO.index(IndexState.WITHOUT, id));
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

        if(bindingResult.hasErrors()) {
            return "people/new";
        }

        personDAO.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String editPerson(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", personDAO.show(id));

        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String updatePerson(@ModelAttribute("person") @Valid Person person,
                               BindingResult bindingResult,
                               @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        personDAO.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        personDAO.delete(id);
        return "redirect:/people";
    }

    @PatchMapping("/{id}/addFriend")
    public String addFriend(@PathVariable("id") int id, @ModelAttribute("friend") Person friend) {
        personDAO.addFriend(id, friend.getId());
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}/addFriend")
    public String deleteFriend(@PathVariable("id") int id, @RequestParam("friendId") int friendId) {
        personDAO.removeFriend(id, friendId);
        return "redirect:/people/{id}";
    }
}
