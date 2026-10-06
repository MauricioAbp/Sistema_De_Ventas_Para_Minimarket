package edu.upn.proyecto.gruposowad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/", "/login", "/mfa", "/pos", "/admin", "/app", "/app/**"})
    public String serveAngularApp() {
        return "forward:/index.html";
    }
}
