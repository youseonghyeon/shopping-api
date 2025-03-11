package com.shop.shoppingapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.security.utils.HttpRequestWrapper;
import com.shop.shoppingapi.security.utils.RsaUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String LOGIN_END_POINT = "/api/login";
    private final String LOGIN_CONTENT_TYPE = "application/json";
    private final RsaUtils rsaUtils;
    private final RememberMeServices rememberMeServices;


    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices, RsaUtils rsaUtils) {
        super(authenticationManager);
        setFilterProcessesUrl(LOGIN_END_POINT);
        this.rememberMeServices = rememberMeServices;
        this.rsaUtils = rsaUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (LOGIN_END_POINT.equals(request.getServletPath())) {
            try {
                // 본문이 없는 경우 기본 처리
                if (request.getContentLength() == 0) {
                    return super.attemptAuthentication(request, response);
                }
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
                String decryptedEmail = rsaUtils.decrypt(loginRequest.getEmail());
                String decryptedPassword = rsaUtils.decrypt(loginRequest.getPassword());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(decryptedEmail, decryptedPassword);
                setDetails(request, token);
                Authentication authenticate = this.getAuthenticationManager().authenticate(token);

                if (loginRequest.isRememberMe()) {
                    // rememberMeService 가 JSON을 처리할줄 몰라서 requestWrapper 를 만들어서 처리함
                    HttpRequestWrapper wrappedRequest = new HttpRequestWrapper(request);
                    wrappedRequest.addParameter("rememberMe", "true");
                    rememberMeServices.loginSuccess(wrappedRequest, response, authenticate);
                }
                return authenticate;
            } catch (RuntimeException e) {
                log.warn("Authentication failed: {}", e.getMessage());
                throw e;
            } catch (IOException e) {
                // JSON 파싱 오류
                log.error("", e);
                throw new AuthenticationServiceException("유효하지 않은 요청입니다.", e);
            } catch (Exception e) {
                // 이외 처리 오류
                log.error("", e);
                throw new AuthenticationServiceException("유효하지 않는 요청입니다.", e);
            }
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

    @Data
    private static class LoginRequest {
        private String email;
        private String password;
        private boolean rememberMe;
    }
}
