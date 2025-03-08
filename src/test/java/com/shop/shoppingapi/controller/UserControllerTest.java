package com.shop.shoppingapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // standaloneSetup: 컨트롤러 단위 테스트용
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 이메일 형식이 올바르지 않을 때 검증 메시지가 반환되는지 확인
     */
    @Test
    @DisplayName("이메일 형식이 올바르지 않을 때 검증 메시지가 반환되는지 확인")
    void signup_whenInvalidEmail_thenReturnValidationErrors() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "invalid-email",      // 잘못된 이메일
                "Test User",
                "password123",
                "password123",
                "01012345678"
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.errors.email").value("이메일 형식이 올바르지 않습니다."));
    }

    /**
     * 비밀번호와 비밀번호 확인이 일치하지 않을 때 검증 메시지가 반환되는지 확인
     */
    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않을 때 검증 메시지가 반환되는지 확인")
    void signup_whenPasswordsMismatch_thenReturnValidationErrors() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "test@example.com",
                "Test User",
                "password123",
                "differentPassword", // 불일치하는 비밀번호 확인
                "01012345678"
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.errors.passwordConfirm").value("비밀번호는 영문/숫자/특수문자 중 2가지 이상 조합 8~20자여야 합니다."));
    }

    /**
     * 모든 필드가 누락된 경우, 각 필드에 대한 검증 에러 메시지가 존재하는지 확인
     */
    @Test
    @DisplayName("모든 필드가 누락된 경우, 각 필드에 대한 검증 에러 메시지가 존재하는지 확인")
    void signup_whenMissingFields_thenReturnValidationErrors() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "", "", "", "", ""
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.errors.email").value("이메일을 입력해주세요."))
                .andExpect(jsonPath("$.errorDetails.errors.name").value("이름을 입력해주세요."))
                .andExpect(jsonPath("$.errorDetails.errors.password").value("비밀번호는 영문/숫자/특수문자 중 2가지 이상 조합 8~20자여야 합니다."))
                .andExpect(jsonPath("$.errorDetails.errors.passwordConfirm").value("비밀번호는 영문/숫자/특수문자 중 2가지 이상 조합 8~20자여야 합니다."))
                .andExpect(jsonPath("$.errorDetails.errors.phone").value("휴대폰 번호 양식에 맞지 않습니다."));
    }
}
