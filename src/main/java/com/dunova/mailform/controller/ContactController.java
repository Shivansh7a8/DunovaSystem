package com.dunova.mailform.controller;

import com.dunova.mailform.model.ContactRequest;
import com.dunova.mailform.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> submitForm(@Valid @RequestBody ContactRequest request) {
        emailService.sendFormEmails(request);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Thank you! Your message has been received and a confirmation email has been sent."
        ));
    }
}
