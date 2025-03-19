package ru.test.ManageSystem.mapper;

import ru.test.ManageSystem.DTO.UserDto;
import ru.test.ManageSystem.entity.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

}