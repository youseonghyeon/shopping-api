package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.CreateUserRequest;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.UserConverter;
import com.shop.shoppingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long createUser(CreateUserRequest request) {
        validateDuplicateUser(request.getEmail(), request.getPhone());
        User newUser = UserConverter.toEntity(request);
        userRepository.save(newUser);
        return newUser.getId();
    }

    private void validateDuplicateUser(String email, String phone) {
        if (userRepository.existsByEmail(email))
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다.", "email");
        if (userRepository.existsByPhone(phone))
            throw new DuplicateResourceException("이미 사용 중인 전화번호입니다.", "phone");
    }

}
