package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.email.EmailRequest;
import com.shop.shoppingapi.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailSenderService emailSenderService;

    @PostMapping("/send/signup")
    public ResponseEntity<ApiResponse<?>> sendEmail(@RequestBody EmailRequest emailRequest) throws MessagingException {
        emailSenderService.sendWelcomeEmail(emailRequest.getEmail(), emailRequest.getUsername());
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
