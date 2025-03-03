package com.shop.shoppingapi.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    void sendEmail() throws MessagingException, InterruptedException {
        emailSenderService.sendWelcomeEmail("dolla_@naver.com", "유성현");
        /// 데몬쓰레드로 설정하지 않았으므로 sleep을 걸어서 메일을 보내는 동안 대기
        Thread.sleep(5000);
    }

}
