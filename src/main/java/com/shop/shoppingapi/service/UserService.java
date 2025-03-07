package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.CreateUserRequest;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.UserConverter;
import com.shop.shoppingapi.exception.UserNotFoundException;
import com.shop.shoppingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createUser(CreateUserRequest request) {
        validateDuplicateUser(request.getEmail(), request.getPhone());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = UserConverter.toEntity(request, encodedPassword);
        userRepository.save(newUser);
        return newUser.getId();
    }

    private void validateDuplicateUser(String email, String phone) {
        if (userRepository.existsByEmail(email))
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다.", "email");
        if (userRepository.existsByPhone(phone))
            throw new DuplicateResourceException("이미 사용 중인 전화번호입니다.", "phone");
    }

    @Transactional
    public void addPoints(Long userId, BigDecimal bigDecimal) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.addPoints(bigDecimal);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
