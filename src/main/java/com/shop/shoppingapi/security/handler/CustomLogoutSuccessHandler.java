package com.shop.shoppingapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Map;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // JSON Body에서 redirectUrl 가져오기
        ObjectMapper objectMapper = new ObjectMapper();
        String redirectUrl = "/"; // 기본값
        try {
            Map<String, String> body = objectMapper.readValue(request.getInputStream(), Map.class);
            if (body.containsKey("redirectUrl")) {
                redirectUrl = body.get("redirectUrl");
            }
        } catch (IOException e) {
            System.out.println("로그아웃 요청에서 redirectUrl을 가져오는 데 실패: " + e.getMessage());
        }

        // JSON 응답 반환
        String jsonResponse = "{\"redirectUrl\":\"" + redirectUrl + "\"}";
        response.getWriter().write(jsonResponse);
    }
}
