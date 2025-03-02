package com.shop.shoppingapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPageController {

    @GetMapping("/mypage")
    public ResponseEntity<String> myPage(Authentication authentication, HttpServletRequest request) {
        boolean hasLoggedIn = authentication != null;
        return ResponseEntity.ok("You are logged in: " + hasLoggedIn);
    }
}
