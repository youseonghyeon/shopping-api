package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.UserRepository;
import com.shop.shoppingapi.utils.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 성공")
    void createUser_Success() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder);
        CreateUserRequest request = UserFixture.toCreateUserRequest(null, null, null, null, null);
        // when
        Long userId = userService.createUser(request);
        // then
        Optional<User> savedUser = userRepository.findById(userId);
        assertTrue(savedUser.isPresent());
        User findUser = savedUser.get();
        assertAll(
                () -> assertEquals(request.getEmail(), findUser.getEmail()),
                () -> assertEquals(request.getName(), findUser.getUsername()),
                () -> assertTrue(passwordEncoder.matches(request.getPassword(), findUser.getPassword())),
                () -> assertEquals(request.getPhone(), findUser.getPhone()),
                () -> assertEquals(Role.ROLE_USER, findUser.getRole()),
                () -> assertNull(findUser.getWishlists())
        );

    }

    @Test
    @DisplayName("회원 가입 실패: 중복된 이메일")
    void createUser_Fail_DuplicateEmail() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder);
        CreateUserRequest request = UserFixture.toCreateUserRequest();
        // when
        userService.createUser(request);
        // then
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("회원 가입 실패: 중복된 전화번호")
    void createUser_Fail_DuplicatePhone() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder);
        CreateUserRequest request = UserFixture.toCreateUserRequest();
        // when
        userService.createUser(request);
        // then
        CreateUserRequest requestWithDuplicatePhone = UserFixture.toCreateUserRequest(null, null, null, null, "01012345678");
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(requestWithDuplicatePhone));
    }

    @Test
    @DisplayName("포인트 추가 성공")
    void addPoints_Success() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder);
        User user = userRepository.save(UserFixture.toUser(null, null, null, null, 100_000, null, null));
        int userPoint = user.getPoint();
        int newPoint = 150_000;
        // when
        userService.addPoints(user.getId(), newPoint);
        // then
        Optional<User> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(userPoint + newPoint, updatedUser.get().getPoint());
    }
}
