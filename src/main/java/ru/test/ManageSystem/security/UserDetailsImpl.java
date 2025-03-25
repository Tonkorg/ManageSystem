package ru.test.ManageSystem.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.test.ManageSystem.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link UserDetails} для предоставления данных пользователя
 * в Spring Security. Содержит email, пароль и роли пользователя.
 */
@Data
@Builder
public class UserDetailsImpl implements UserDetails {

    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Создаёт объект {@link UserDetailsImpl} на основе сущности {@link User}.
     * Преобразует роли пользователя в объекты {@link GrantedAuthority}, добавляя префикс "ROLE_".
     *
     * @param user объект {@link User} с данными пользователя
     * @return новый экземпляр {@link UserDetailsImpl}
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return UserDetailsImpl.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    /**
     * Возвращает коллекцию ролей (прав доступа) пользователя.
     *
     * @return коллекция объектов {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return строка с паролем
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя (email).
     *
     * @return строка с email пользователя
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Указывает, не истёк ли срок действия учётной записи.
     *
     * @return {@code true}, так как срок действия не проверяется
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учётная запись.
     *
     * @return {@code true}, так как блокировка не реализована
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истёк ли срок действия учетных данных (пароля).
     *
     * @return {@code true}, так как срок действия пароля не проверяется
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активна ли учётная запись.
     *
     * @return {@code true}, так как отключение учётной записи не реализовано
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}