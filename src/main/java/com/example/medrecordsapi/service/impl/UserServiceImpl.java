package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.dto.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.UserResponseDto;
import com.example.medrecordsapi.exception.RegistrationException;
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

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException("Can't register this user.");
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
