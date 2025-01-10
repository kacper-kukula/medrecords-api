package com.example.medrecordsapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.medrecordsapi.dto.UserLoginRequestDto;
import com.example.medrecordsapi.dto.UserLoginResponseDto;
import com.example.medrecordsapi.security.AuthenticationService;
import com.example.medrecordsapi.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Test login with valid credentials")
    public void authenticate_ValidCredentials_ReturnsJwtToken() {
        String token = "valid-jwt-token";
        String email = "valid@example.com";
        String password = "password123";
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        UserLoginResponseDto response = authenticationService.authenticate(requestDto);

        assertThat(response.token()).isEqualTo(token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(email);
    }

    @Test
    @DisplayName("Test login with bad credentials")
    void authenticate_InvalidCredentials_ThrowsBadCredentialsException() {
        String email = "invalid@example.com";
        String password = "wrongPassword";
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationService.authenticate(requestDto))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
