package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.MyRoomResponse;
import com.shop.shoppingapi.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPageController {

    @GetMapping("/mypage")
    public ResponseEntity<? extends ApiResponse<?>> myPage(Authentication authentication) {
        if (authentication == null) {
            return ApiResponse.error("인증 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        User principal = (User) authentication.getPrincipal();
        MyRoomResponse myRoomResponse = new MyRoomResponse(principal.getUsername(), principal.getRole().name(), true);
        return ApiResponse.success(myRoomResponse);

    }
}
