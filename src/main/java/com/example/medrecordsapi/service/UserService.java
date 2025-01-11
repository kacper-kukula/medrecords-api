package com.example.medrecordsapi.service;

import com.example.medrecordsapi.dto.user.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.user.UserResponseDto;
import com.example.medrecordsapi.exception.custom.RegistrationException;

public interface UserService {

    UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
