package com.example.medrecordsapi.controller;

import com.example.medrecordsapi.dto.user.UserLoginRequestDto;
import com.example.medrecordsapi.dto.user.UserLoginResponseDto;
import com.example.medrecordsapi.dto.user.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.user.UserResponseDto;
import com.example.medrecordsapi.exception.custom.RegistrationException;
import com.example.medrecordsapi.security.AuthenticationService;
import com.example.medrecordsapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "User registration and login endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user with the provided details. "
                    + "Throws a RegistrationException if the user already exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User successfully registered"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, validation error or existing email")})
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        log.info("Received request to register a new user with email: {}", requestDto.email());
        UserResponseDto response = userService.registerUser(requestDto);
        log.info("User registered successfully with ID: {}", response.id());

        return response;
    }

    @Operation(summary = "Login an existing user",
            description = "Authenticates the user with the provided "
                    + "login credentials and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials")})
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        log.info("Received login request for email: {}", requestDto.email());
        UserLoginResponseDto response = authenticationService.authenticate(requestDto);
        log.info("User successfully authenticated with email: {}", requestDto.email());

        return response;
    }
}
