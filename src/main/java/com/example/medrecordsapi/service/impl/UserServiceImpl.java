package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.dto.user.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.user.UserResponseDto;
import com.example.medrecordsapi.exception.custom.RegistrationException;
import com.example.medrecordsapi.mapper.UserMapper;
import com.example.medrecordsapi.model.Role;
import com.example.medrecordsapi.model.User;
import com.example.medrecordsapi.repository.UserRepository;
import com.example.medrecordsapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String REGISTRATION_FAIL_MESSAGE = "Can't register this user";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmailIgnoreCase(requestDto.email()).isPresent()) {
            throw new RegistrationException(REGISTRATION_FAIL_MESSAGE);
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
