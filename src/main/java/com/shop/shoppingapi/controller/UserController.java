package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CreateUserRequest;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.UserRepository;
import com.shop.shoppingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<? extends ApiResponse<?>> signup(@Validated @RequestBody CreateUserRequest createUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ApiResponse.bindingResultError("요청 데이터가 올바르지 않습니다.", bindingResult);
        }
        Long user = userService.createUser(createUserRequest);
        log.info("Created user with id {}", user);
        return ApiResponse.success("회원가입을 성공하였습니다.", "회원가입을 성공하였습니다.");
    }



    private final UserRepository userRepository;
    @GetMapping("/test")
    public void test() {
        userRepository.findAll().forEach(a -> log.info("{}",a));
    }


}
