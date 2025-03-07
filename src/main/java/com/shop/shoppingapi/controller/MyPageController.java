package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.MyRoomResponse;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity<? extends ApiResponse<?>> myPage(Authentication authentication) {
        if (authentication == null) {
            return ApiResponse.error("인증 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        Long userId = SecurityUtils.getUserId();
        User user = userService.findById(userId);
        MyRoomResponse myRoomResponse = new MyRoomResponse(user.getUsername(), user.getRole().name(), true, user.getPoint());
        return ApiResponse.success(myRoomResponse);

    }
}
