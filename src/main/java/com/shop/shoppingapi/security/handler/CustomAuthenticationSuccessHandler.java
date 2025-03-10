package com.shop.shoppingapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.security.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("message", "로그인 성공");
        data.put("user", userDetails.getUsername());

        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        String token = jwtTokenProvider.generateToken(authentication);
//
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setContentType("application/json;charset=UTF-8");
//
//        // 로그인한 사용자 정보 가져오기
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("status", "success");
//        data.put("message", "로그인 성공");
//        data.put("user", userDetails.getUsername());
//        data.put("token", token);
//
//        response.getWriter().write(objectMapper.writeValueAsString(data));
//    }

}
