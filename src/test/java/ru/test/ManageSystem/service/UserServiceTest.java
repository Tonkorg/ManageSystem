package ru.test.ManageSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.exception.UserAlreadyExistsException;
import ru.test.ManageSystem.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encodedPassword")
                .roles(Collections.singleton("USER"))
                .build();

        userCreateDto = UserCreateDto.builder()
                .email("user@example.com")
                .password("password123")
                .roles(Collections.singleton("USER"))
                .build();
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        when(userRepository.findByEmail(userCreateDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userCreateDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(userCreateDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowUserAlreadyExistsException() {
        when(userRepository.findByEmail(userCreateDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userCreateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("user@example.com");

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }
}