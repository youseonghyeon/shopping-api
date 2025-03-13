package com.shop.shoppingapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 이메일 제목 & 수신자 설정
        helper.setTo(toEmail);
        helper.setSubject("🎉 회원가입을 환영합니다, " + username + "님!");
        helper.setFrom(fromEmail);

        // 이메일 본문 (HTML)
        String htmlContent = buildWelcomeEmailContent(username);
        helper.setText(htmlContent, true);  // true: HTML 형식 전송

        // 이메일 전송 (비동기)
        CompletableFuture.runAsync(() -> mailSender.send(message), executorService);
    }

    private String buildWelcomeEmailContent(String username) {
        return String.format("""
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            padding: 20px;
                        }
                        .container {
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                            text-align: center;
                        }
                        h1 {
                            color: #333333;
                        }
                        .button {
                            display: inline-block;
                            padding: 10px 20px;
                            margin-top: 20px;
                            color: #ffffff;
                            background-color: #007bff;
                            text-decoration: none;
                            border-radius: 5px;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 12px;
                            color: #888888;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🎉 환영합니다, %s 님!</h1>
                        <p>회원가입을 축하드립니다! 이제 더 많은 기능을 사용할 수 있습니다.</p>
                        <a href="https://your-website.com/login" class="button">로그인하러 가기</a>
                        <p class="footer">© 2025 YourCompany. All Rights Reserved.</p>
                    </div>
                </body>
                </html>
                """, username);
    }
}
