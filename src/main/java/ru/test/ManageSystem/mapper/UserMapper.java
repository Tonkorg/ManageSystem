package ru.test.ManageSystem.mapper;

import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.entity.User;

/**
 * Утилитный класс для преобразования сущности {@link User} в объект передачи данных {@link UserDto}.
 */
public class UserMapper {

    /**
     * Преобразует сущность {@link User} в объект {@link UserDto}.
     * Копирует идентификатор, email и роли пользователя.
     *
     * @param user сущность {@link User}, представляющая пользователя
     * @return объект {@link UserDto} с данными пользователя
     */
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}