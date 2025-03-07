package com.shop.shoppingapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String LOGIN_END_POINT = "/api/login";
    private final String LOGIN_CONTENT_TYPE = "application/json";

    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl(LOGIN_END_POINT);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (LOGIN_END_POINT.equals(request.getServletPath()) && LOGIN_CONTENT_TYPE.equals(request.getContentType())) {
            try {
                // 본문이 없는 경우 기본 처리
                if (request.getContentLength() == 0) {
                    return super.attemptAuthentication(request, response);
                }
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
                setDetails(request, token);
                return this.getAuthenticationManager().authenticate(token);
            } catch (IOException e) {
                throw new AuthenticationServiceException("JSON 파싱 실패", e);
            }
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

    @Getter
    private static class LoginRequest {
        private String username;
        private String password;
    }
}
