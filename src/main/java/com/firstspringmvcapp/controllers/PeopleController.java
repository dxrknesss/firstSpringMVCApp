package com.firstspringmvcapp.controllers;

import com.firstspringmvcapp.dto.UserDto;
import com.firstspringmvcapp.models.User;
import com.firstspringmvcapp.services.PeopleService;
import com.firstspringmvcapp.services.MinioFileService;
import com.firstspringmvcapp.util.PersonErrorResponse;
import com.firstspringmvcapp.util.PersonValidator;
import com.firstspringmvcapp.util.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                       @ModelAttribute("friend") User friend,
                       HttpServletResponse servletResponse) {
        servletResponse.addHeader("Cache-control", "no-cache");
        servletResponse.addHeader("Pragma", "no-cache");
        servletResponse.addHeader("Expires", "0");
        User user = peopleService.findOne(id);

        model.addAttribute("user", user);
        model.addAttribute("friends", peopleService.findFriends(id));
        model.addAttribute("availableFriends", peopleService.findAvailableFriends(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("userDto") UserDto userDto) {
        return "people/new";
    }

    @PostMapping()
    public String createPerson(@ModelAttribute("userDto") @Valid UserDto userDto,
                               BindingResult bindingResult) {
        personValidator.validate(userDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        User user = peopleService.userDtoToUser(userDto);
        peopleService.save(user);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String editPerson(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", peopleService.findOne(id));

        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String updatePerson(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                               @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, user);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }

    @PostMapping("/{id}/friends")
    public String addFriend(@PathVariable("id") int userId,
                            @ModelAttribute("friend") User friend) {
        peopleService.addFriendById(userId, friend.getId());
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}/friends")
    public String deleteFriend(@PathVariable("id") int personId,
                               @RequestParam("friendId") int friendId) {
        peopleService.removeFriendById(personId, friendId);
        return "redirect:/people/{id}";
    }

    @PostMapping("/{id}/uploadForm")
    public String uploadProfilePicture(@PathVariable("id") int personId,
                                       @RequestParam MultipartFile file) {
        peopleService.uploadProfilePicture(file, personId);

        return "redirect:/people/{id}";
    }
}