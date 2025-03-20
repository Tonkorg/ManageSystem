package ru.test.ManageSystem.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank(message = "необходим email")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Необходим пароль")
    @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
    private String password;

    @Builder.Default
    @NotEmpty(message = "Роли не могут быть пустыми")
    private Set<String> roles = new HashSet<>();
}