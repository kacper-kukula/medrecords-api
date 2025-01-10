package com.example.medrecordsapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.medrecordsapi.dto.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.UserResponseDto;
import com.example.medrecordsapi.exception.custom.RegistrationException;
import com.example.medrecordsapi.mapper.UserMapper;
import com.example.medrecordsapi.model.Role;
import com.example.medrecordsapi.model.User;
import com.example.medrecordsapi.repository.UserRepository;
import com.example.medrecordsapi.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Test user registration with valid email and password")
    void registerUser_ValidCredentials_ReturnsUserResponseDto() throws RegistrationException {
        String email = "valid@example.com";
        String password = "password123";
        User user = new User(
                "12345", email, password, "firstName", "lastName",
                Role.USER);
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                email, password, password,
                user.getFirstName(), user.getLastName());
        UserResponseDto expectedResponse = new UserResponseDto(
                user.getId(), email, user.getFirstName(),
                user.getLastName(), user.getRole());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.registerUser(requestDto);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Test user registration when email already exists")
    void registerUser_EmailAlreadyExists_ThrowsRegistrationException() {
        String email = "existing@example.com";
        String password = "password123";
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                email, password, password, "firstName", "lastName");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.registerUser(requestDto))
                .isInstanceOf(RegistrationException.class);

        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Test password encoding during user registration")
    void registerUser_ValidPassword_PasswordEncodedCorrectly() throws RegistrationException {
        String email = "valid@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                email, password, password, "firstName", "lastName");
        User user = new User("1", email, encodedPassword, "firstName", "lastName", Role.USER);
        UserResponseDto expectedResponse = new UserResponseDto(
                "1", email, "firstName", "lastName", Role.USER);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.registerUser(requestDto);

        assertThat(actualResponse.role()).isEqualTo(Role.USER);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verify(passwordEncoder, times(1)).encode(password);
    }
}
