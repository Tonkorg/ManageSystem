package ru.test.ManageSystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.exception.UserAlreadyExistsException;
import ru.test.ManageSystem.mapper.UserMapper;
import ru.test.ManageSystem.repository.UserRepository;
import ru.test.ManageSystem.security.UserDetailsImpl;

/**
 * Сервис для управления пользователями.
 * Реализует функциональность создания пользователей, получения информации о них
 * и предоставления данных для аутентификации через Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Загружает данные пользователя по email для аутентификации в Spring Security.
     *
     * @param email адрес электронной почты пользователя
     * @return объект {@link UserDetails}, содержащий данные пользователя для аутентификации
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return UserDetailsImpl.build(user);
    }

    /**
     * Создаёт нового пользователя на основе переданных данных.
     * Проверяет уникальность email и шифрует пароль перед сохранением.
     *
     * @param dto объект {@link UserCreateDto} с данными для создания пользователя
     * @return объект {@link UserDto}, представляющий созданного пользователя
     * @throws UserAlreadyExistsException если пользователь с таким email уже существует
     */
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(dto.getRoles())
                .build();
        return UserMapper.toDto(userRepository.save(user));
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     * @return объект {@link User}, представляющий пользователя
     * @throws ResourceNotFoundException если пользователь с указанным email не найден
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Возвращает текущего аутентифицированного пользователя.
     * Извлекает email из контекста безопасности Spring Security и получает данные пользователя.
     *
     * @return объект {@link User}, представляющий текущего пользователя
     * @throws ResourceNotFoundException если пользователь не найден в базе данных
     */
    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();
        return getUserByEmail(email);
    }
}