package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.controller.dto.user.FindEmailRequest;
import com.shop.shoppingapi.controller.dto.user.ResetPassword;
import com.shop.shoppingapi.controller.dto.user.SimpleUserResponse;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${signup.event.point:10000}")
    private Integer SIGNUP_EVENT_POINT;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SimpleUserResponse>> me() {
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

    @PostMapping("/find-email")
    public ResponseEntity<? extends ApiResponse<?>> findEmail(@Validated @RequestBody FindEmailRequest findEmailRequest) {
        // TODO 이메일 찾아서 return 해주는 것으로 함
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/reset-password/send")
    public ResponseEntity<? extends ApiResponse<?>> resetPasswordSendEmailCode(@Validated @RequestBody ResetPassword resetPassword) {
        // TODO 이메일로 발송 -> 발송하면서 db UUID update
        // 이메일로 http://www.xx.shop/confirm-?uuid=xxxxx 링크를 주고 링크를 타면 url 이동
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<? extends ApiResponse<?>> resetPasswordConfirm(@Validated @RequestBody ResetPassword resetPassword) {
        // 성공 결과 주면서 신규 UUID를 발급 전송
        // 신규 UUID는 사용자가 페이지에 저장했다가 비밀번호 초기화 요청에 사용함
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<? extends ApiResponse<?>> resetPassword(@Validated @RequestBody ResetPassword resetPassword) {
        // 신규 UUID를 받아서 비밀번호 초기화
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
