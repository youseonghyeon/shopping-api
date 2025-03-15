package com.shop.shoppingapi.controller.dto.user;

import com.shop.shoppingapi.aop.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@FieldMatch(first = "password", second = "passwordConfirm", message = "비밀번호가 일치하지 않습니다.")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Pattern(
            regexp = "^(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[!@#$%^&*])|(?=.*\\d)(?=.*[!@#$%^&*])).{8,20}$",
            message = "비밀번호는 영문/숫자/특수문자 중 2가지 이상 조합 8~20자여야 합니다."
    )
    private String password;

    @Pattern(
            regexp = "^(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[!@#$%^&*])|(?=.*\\d)(?=.*[!@#$%^&*])).{8,20}$",
            message = "비밀번호는 영문/숫자/특수문자 중 2가지 이상 조합 8~20자여야 합니다."
    )
    private String passwordConfirm;

    @Pattern(regexp = "\\d{10,11}", message = "휴대폰 번호 양식에 맞지 않습니다.")
    private String phone;

}
