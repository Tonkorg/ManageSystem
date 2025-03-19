package ru.test.ManageSystem.service;


import ru.test.ManageSystem.DTO.UserCreateDto;
import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.exception.UserAlreadyExistsException;
import ru.test.ManageSystem.mapper.UserMapper;
import ru.test.ManageSystem.repository.UserRepository;
import ru.test.ManageSystem.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return UserDetailsImpl.build(user);
    }

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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();
        return getUserByEmail(email);
    }
}