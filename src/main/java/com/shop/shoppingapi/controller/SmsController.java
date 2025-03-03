package com.shop.shoppingapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @PostMapping("/send")
    public void sendSms() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
