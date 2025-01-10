package com.example.medrecordsapi.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.medrecordsapi.model.User;
import com.example.medrecordsapi.repository.UserRepository;
import com.example.medrecordsapi.security.AuthenticationService;
import com.example.medrecordsapi.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        User user = new User();

        userService.register(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testLoginUser() {
        AuthenticationService authenticationService = new AuthenticationService();

        String token = authenticationService.authenticate();
        System.out.println(token);

        Assertions.assertThat(token).isNotEmpty();
    }
}
