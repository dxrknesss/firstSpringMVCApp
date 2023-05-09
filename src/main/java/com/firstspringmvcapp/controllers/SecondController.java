package com.firstspringmvcapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecondController {
    @GetMapping("/")
    public String greetingsPage() {
        return "redirect:/people";
    }
}
