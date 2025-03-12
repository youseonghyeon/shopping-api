package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.controller.dto.user.SimpleUserResponse;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final Integer SIGNUP_EVENT_POINT = 1_000_000;


    @GetMapping("/me")
    public ResponseEntity< ApiResponse<SimpleUserResponse>> me() {
        Long userId = SecurityUtils.getUserId();
        User findUser = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        return ApiResponse.success(SimpleUserResponse.from(findUser), "사용자 정보를 조회하였습니다.");
    }

    @PostMapping("/signup")
    public ResponseEntity<? extends ApiResponse<?>> signup(@Validated @RequestBody CreateUserRequest createUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ApiResponse.bindingResultError("요청 데이터가 올바르지 않습니다.", bindingResult);
        }
        Long userId = userService.createUser(createUserRequest);
        log.info("Created user with id {}", userId);

        userService.addPoints(userId, SIGNUP_EVENT_POINT);
        String data = "회원가입을 성공하였습니다. " + SIGNUP_EVENT_POINT + "point 가 지급되었습니다.";
        return ApiResponse.success(data, "회원가입을 성공하였습니다.");
    }
}
