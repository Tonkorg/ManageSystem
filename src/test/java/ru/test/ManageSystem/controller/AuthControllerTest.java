package ru.test.ManageSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.test.ManageSystem.DTO.AuthRequest;
import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.repository.UserRepository;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        userRepository.deleteAll();

        // Добавление тестового пользователя с закодированным паролем
        User existingUser = User.builder()
                .email("existing@example.com")
                .password(passwordEncoder.encode("password123"))
                .roles(Collections.singleton("USER"))
                .build();
        userRepository.save(existingUser);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        UserCreateDto request = UserCreateDto.builder()
                .email("newuser@example.com")
                .password("password123")
                .roles(Collections.singleton("USER"))
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        UserCreateDto request = UserCreateDto.builder()
                .email("existing@example.com")
                .password("password123")
                .roles(Collections.singleton("USER"))
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthRequest request = new AuthRequest("existing@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLoginBadCredentials() throws Exception {
        AuthRequest request = new AuthRequest("existing@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный email или пароль"));
    }
}