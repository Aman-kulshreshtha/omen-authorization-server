package com.security.omen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/.well-known")
public class WellKnownFallbackController {

    @GetMapping("/appspecific/{filename:.+}")
    public ResponseEntity<Void> ignoreChromeRequest(@PathVariable String filename) {
        return ResponseEntity.noContent().build();  // return 204 No Content
    }
}
