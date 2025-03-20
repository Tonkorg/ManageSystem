package ru.test.ManageSystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.ManageSystem.DTO.AuthRequest;
import ru.test.ManageSystem.DTO.AuthResponse;

import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.exception.ErrorResponse;
import ru.test.ManageSystem.exception.UserAlreadyExistsException;
import ru.test.ManageSystem.security.JwtTokenProvider;
import ru.test.ManageSystem.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и регистрации")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и возвращает JWT токен")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }


}
