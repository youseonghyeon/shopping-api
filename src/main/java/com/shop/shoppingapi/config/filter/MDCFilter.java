package com.shop.shoppingapi.config.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString(); // 고유한 트레이스 ID 생성
            String slicedTransactionId = traceId.substring(0, 8);
            MDC.put("traceId", slicedTransactionId); // MDC에 traceId 저장
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // 요청이 끝나면 MDC 값 삭제 (메모리 누수 방지)
        }
    }
}
