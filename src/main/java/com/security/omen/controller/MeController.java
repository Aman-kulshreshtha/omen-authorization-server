package com.security.omen.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @SecurityRequirement(name = "oauth2")
    @GetMapping("/me")
    public ResponseEntity<String> getProfile() {
        return ResponseEntity.ok("This is a secure endpoint");
    }
}
