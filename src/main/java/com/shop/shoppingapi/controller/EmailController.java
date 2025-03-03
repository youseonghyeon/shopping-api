package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.service.EmailSenderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailSenderService emailSenderService;

    @PostMapping("/send/signup")
    public ResponseEntity<ApiResponse<?>> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailSenderService.sendEmail(emailRequest.getEmail(), "회원가입을 축하합니다.", "회원가입을 축하합니다.");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Getter
    public static class EmailRequest {
        private String email;
    }
}
