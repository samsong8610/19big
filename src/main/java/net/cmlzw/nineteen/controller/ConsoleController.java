package net.cmlzw.nineteen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/console")
public class ConsoleController {
    @RequestMapping
    public String console() {
        return "console";
    }
}
