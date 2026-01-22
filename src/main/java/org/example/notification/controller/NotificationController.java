package org.example.notification.controller;

import org.example.notification.dto.SendEmailDto;
import org.example.notification.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody SendEmailDto sendEmailDto) {
        emailService.sendEmail(
            sendEmailDto.getEmail(),
            sendEmailDto.getSubject(),
            sendEmailDto.getMessage()
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
