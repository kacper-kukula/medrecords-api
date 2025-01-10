package com.example.medrecordsapi.service;

import com.example.medrecordsapi.dto.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.UserResponseDto;
import com.example.medrecordsapi.exception.custom.RegistrationException;

public interface UserService {

    UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
