package com.example.medrecordsapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.medrecordsapi.dto.user.UserLoginRequestDto;
import com.example.medrecordsapi.dto.user.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.user.UserResponseDto;
import com.example.medrecordsapi.model.Role;
import com.example.medrecordsapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    private static final String AUTH_REGISTER_PATH = "/auth/register";
    private static final String AUTH_LOGIN_PATH = "/auth/login";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String INVALID_EMAIL = "invalid-email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();
    }

    private void createAndSaveUser(String email, String password, String first, String last,
                                   Role role) {
        User user = new User(null, email, passwordEncoder.encode(password), first, last, role);
        mongoTemplate.save(user);
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Register new user successfully")
        void registerUser_ValidData_ReturnsUserResponseDto() throws Exception {
            UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);
            UserResponseDto responseDto = new UserResponseDto(
                    null, TEST_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, Role.USER);

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.email").value(responseDto.email()))
                    .andExpect(jsonPath("$.firstName").value(responseDto.firstName()))
                    .andExpect(jsonPath("$.lastName").value(responseDto.lastName()))
                    .andExpect(jsonPath("$.role").value(responseDto.role().toString()));
        }

        @Test
        @DisplayName("Register user with existing email")
        void registerUser_UserWithExistingEmail_ReturnsRegistrationException() throws Exception {
            createAndSaveUser(TEST_EMAIL, TEST_PASSWORD,
                    TEST_FIRST_NAME, TEST_LAST_NAME, Role.USER);
            UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                    TEST_EMAIL, "newPassword123", "newPassword123", "Bob", "Smith");

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @Test
        @DisplayName("Register user with invalid email")
        void registerUser_InvalidEmail_ReturnsBadRequest() throws Exception {
            UserRegistrationRequestDto invalidRequest = new UserRegistrationRequestDto(
                    INVALID_EMAIL, TEST_PASSWORD, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @Test
        @DisplayName("Register user with null email")
        void registerUser_EmailNull_ReturnsBadRequest() throws Exception {
            UserRegistrationRequestDto invalidRequest = new UserRegistrationRequestDto(
                    null, TEST_PASSWORD, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME);

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @ParameterizedTest
        @CsvSource({
                "p",
                "pas",
                "passwor",
                "exactly21charactersss",
                "tooLongPasswordAbove20Chars",
                "obviouslyWaaaayTooLongPasswordSurpassingWellAbove20Chars"
        })
        @DisplayName("Register user with password too short or too long")
        void registerUser_InvalidPasswordLength_ReturnsBadRequest(String password)
                throws Exception {
            UserRegistrationRequestDto invalidRequest = new UserRegistrationRequestDto(
                    TEST_EMAIL, password, password, TEST_FIRST_NAME, TEST_LAST_NAME);

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @Test
        @DisplayName("Register user with password mismatch")
        void registerUser_PasswordMismatch_ReturnsBadRequest() throws Exception {
            UserRegistrationRequestDto invalidRequest = new UserRegistrationRequestDto(
                    TEST_EMAIL, TEST_PASSWORD, "differentPass", TEST_FIRST_NAME, TEST_LAST_NAME);

            mockMvc.perform(post(AUTH_REGISTER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Login user successfully")
        void login_ValidCredentials_ReturnsJwtToken() throws Exception {
            createAndSaveUser(TEST_EMAIL, TEST_PASSWORD,
                    TEST_FIRST_NAME, TEST_LAST_NAME, Role.USER);
            UserLoginRequestDto requestDto = new UserLoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

            mockMvc.perform(post(AUTH_LOGIN_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Login with non-existing user")
        void login_NonExistingUser_ReturnsUnauthorized() throws Exception {
            UserLoginRequestDto requestDto =
                    new UserLoginRequestDto("nonexistent@example.com", TEST_PASSWORD);

            mockMvc.perform(post(AUTH_LOGIN_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @ParameterizedTest
        @CsvSource({
                "invalid-email, password123",
                ", password123",
                "test@example.com, ",
                "test@example.com, tooLongPasswordAbove20Chars",
                ","
        })
        @DisplayName("Login with invalid inputs")
        void login_InvalidInputs_ReturnsBadRequest(String email, String password) throws Exception {
            UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);

            mockMvc.perform(post(AUTH_LOGIN_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }

        @Test
        @DisplayName("Login with case-insensitive email")
        void login_CaseInsensitiveEmail_ReturnsJwtToken() throws Exception {
            createAndSaveUser(TEST_EMAIL.toLowerCase(), TEST_PASSWORD,
                    TEST_FIRST_NAME, TEST_LAST_NAME, Role.USER);
            UserLoginRequestDto requestDto =
                    new UserLoginRequestDto(TEST_EMAIL.toUpperCase(), TEST_PASSWORD);

            mockMvc.perform(post(AUTH_LOGIN_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Login with case-insensitive password")
        void login_CaseInsensitivePassword_ReturnsUnauthorized() throws Exception {
            createAndSaveUser(TEST_EMAIL, "testPaSSword",
                    TEST_FIRST_NAME, TEST_LAST_NAME, Role.USER);
            UserLoginRequestDto requestDto =
                    new UserLoginRequestDto(TEST_EMAIL, "testPassword");

            mockMvc.perform(post(AUTH_LOGIN_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }
    }
}
