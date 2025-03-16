package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final RestTemplate restTemplate;

    private Boolean isOpen = false;
    // L4 로드벨런서를 가정하고 작성
    String eventL4Ip = "127.0.0.1";
    int eventL4Port = 8090;

    // 이벤트가 열렸는지 확인
    @GetMapping("/event/open")
    public ResponseEntity<ApiResponse<Boolean>> openEvent() {
        String message = isOpen ? "이벤트가 열렸습니다." : "이벤트가 닫혔습니다.";
        return ApiResponse.success(isOpen, message);
    }

    // 이벤트 잔여 수량이 있는지 확인
    @GetMapping("/event/coupon/status")
    public ResponseEntity<ApiResponse<String>> getCoupon() {

        String couponStatusUrl = "http://" + eventL4Ip + ":" + eventL4Port + "/api/coupon/status";
        String response = restTemplate.getForObject(couponStatusUrl, String.class);
        String parsedData = response;
        return ApiResponse.success(parsedData, "이벤트 페이지 접근이 가능합니다.");
    }

    @PostMapping("/event/close")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> closeEvent() {
        this.isOpen = false;
        Map<String, Object> responseData = Map.of("isOpen", isOpen, "message", "이벤트가 닫혔습니다.");
        return ApiResponse.success(responseData, "이벤트가 닫혔습니다.");
    }

    @PostMapping("/event/open")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> openOrder() {
        this.isOpen = true;
        Map<String, Object> responseData = Map.of("isOpen", isOpen, "message", "이벤트가 닫혔습니다.");
        return ApiResponse.success(responseData, "이벤트가 열렸습니다.");
    }
}
