package com.shop.shoppingapi.gateway;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.TicketApplyRequest;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventGateway {

    private final RestTemplate restTemplate;

    private Boolean isOpen = true;

    @Value("${event.l4.url}")
    private String eventL4Url;

    // 이벤트가 열렸는지 확인
    @GetMapping("/open")
    public ResponseEntity<ApiResponse<Boolean>> openEvent() {
        String message = isOpen ? "이벤트가 열렸습니다." : "이벤트가 닫혔습니다.";
        return ApiResponse.success(isOpen, message);
    }

    // 이벤트 잔여 수량이 있는지 확인
    @GetMapping("/coupon/status")
    public ResponseEntity<ApiResponse<String>> getCoupon() {
        String couponStatusUrl = eventL4Url + "/api/coupon/status";
        String response = restTemplate.getForObject(couponStatusUrl, String.class);
        String parsedData = response; // TODO data parsing
        return ApiResponse.success(parsedData, "이벤트 페이지 접근이 가능합니다.");
    }

    @PostMapping("/close")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> closeEvent() {
        this.isOpen = false;
        Map<String, Object> responseData = Map.of("isOpen", isOpen, "message", "이벤트가 닫혔습니다.");
        return ApiResponse.success(responseData, "이벤트가 닫혔습니다.");
    }

    @PostMapping("/open")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> openOrder() {
        this.isOpen = true;
        Map<String, Object> responseData = Map.of("isOpen", isOpen, "message", "이벤트가 닫혔습니다.");
        return ApiResponse.success(responseData, "이벤트가 열렸습니다.");
    }

    @PostMapping("/ticket/apply")
    public ResponseEntity<? extends ApiResponse<?>> applyEvent() {
        if (!isOpen) {
            return ApiResponse.error("이벤트가 열리지 않았습니다.", HttpStatus.BAD_REQUEST);
        }
        Long userId = SecurityUtils.getUserId();
        log.info("Request to apply event: {}", userId);
        try {
            String RUN_EVENT = eventL4Url + "/event/ticket/apply";
            TicketApplyRequest body = new TicketApplyRequest(userId, 1, "이벤트 참여");
            log.info("request participate event. body = {}", body);
            EventApplyResponse eventApplyResponse = restTemplate.postForObject(RUN_EVENT, body, EventApplyResponse.class);
            log.info("parsedData={}", eventApplyResponse);
            return ApiResponse.success(eventApplyResponse, "이벤트 페이지 접근이 가능합니다.");
        } catch (HttpClientErrorException e) {
            EventApplyResponse responseBodyAs = e.getResponseBodyAs(EventApplyResponse.class);
            log.warn("{}", responseBodyAs);
            return ApiResponse.error("이벤트 참여에 실패하였습니다.", HttpStatus.BAD_REQUEST, responseBodyAs);
        } catch (Exception e) {
            log.error("", e);
            return ApiResponse.error("이벤트 참여에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record EventStatusResponse(boolean available, int remainingTickets) {
    }

    record EventApplyResponse(boolean success, String message) {
    }
}
