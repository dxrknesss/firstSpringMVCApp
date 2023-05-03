package com.firstspringmvcapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecondController {

    @GetMapping("/")
    public String greetingsPage(
            /*@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            Model model*/) {
        // System.out.println("Hello, " + name + " " + surname);
        //model.addAttribute("msg", "Hello, " + name + " " + surname);

        return "redirect:/people";
        //return "second/greetings";
    }

/*    @GetMapping("/bye")
    public String byePage() {
        return "second/bye";
    }

    @GetMapping("/exit")
    public String exitPage() {
        return "second/exit";
    }

    @GetMapping("/calc")
    public String calcPage(
            @RequestParam("a") int a, @RequestParam("b") int b,
            @RequestParam("action") char action,
            Model model) {

        double result = 0;
        switch (action) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                if (b == 0) {
                    model.addAttribute("result", "Division by 0 is stupid!!!");
                } else {
                    result = a / (double)b;
                }
                break;
        }
        model.addAttribute("result", a + " " + action + " " + b + " = " + result);

        return "second/calc";
    }*/
}
