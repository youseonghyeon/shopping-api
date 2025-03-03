package com.shop.shoppingapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 IP, URL, 파라미터 로깅
        String clientIp = request.getRemoteAddr();
        String requestUrl = request.getRequestURI();
        String queryParams = request.getParameterMap().entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));

        // ✅ Request Body 읽기 (ContentCachingRequestWrapper 사용)
        String requestBody = getRequestBody(request);

        log.info("IP: [{}], URL: [{}], Params: [{}], Body: [{}]", clientIp, requestUrl, queryParams, requestBody);
        return true;
    }

    private String getRequestBody(HttpServletRequest request) {
        // 기존 요청을 캐싱된 요청으로 감싸기
        if (!(request instanceof ContentCachingRequestWrapper)) {
            return "Cannot read body";
        }
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;

        // 캐싱된 바디 가져오기
        byte[] content = wrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "No body content";
        }

        try {
            return new String(content, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Failed to read body";
        }
    }
}
