package ru.test.ManageSystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.ManageSystem.DTO.AuthRequest;
import ru.test.ManageSystem.DTO.AuthResponse;
import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.security.JwtTokenProvider;
import ru.test.ManageSystem.service.UserService;

/**
 * Контроллер для управления аутентификацией и регистрацией пользователей.
 * Предоставляет эндпоинты для входа в систему и создания новых пользователей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и регистрации")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * Выполняет вход пользователя в систему.
     * Принимает учетные данные (email и пароль), аутентифицирует пользователя
     * и возвращает JWT-токен в случае успеха.
     *
     * @param request объект с данными для аутентификации (email и пароль)
     * @return ResponseEntity с объектом {@link AuthResponse}, содержащим JWT-токен
     * @throws org.springframework.security.authentication.BadCredentialsException если учетные данные неверны
     */
    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и возвращает JWT токен")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Принимает данные для создания пользователя и возвращает информацию о созданном пользователе.
     *
     * @param dto объект {@link UserCreateDto} с данными нового пользователя
     * @return ResponseEntity с объектом {@link UserDto}, представляющим созданного пользователя
     * @throws ru.test.ManageSystem.exception.UserAlreadyExistsException если пользователь с таким email уже существует
     * @throws jakarta.validation.ConstraintViolationException если данные в запросе некорректны
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }
}