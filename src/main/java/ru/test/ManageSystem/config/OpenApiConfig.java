package ru.test.ManageSystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger) для документации API.
 * Определяет метаданные API и схему авторизации с использованием JWT.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создаёт и настраивает объект {@link OpenAPI} для генерации документации Swagger.
     * Устанавливает информацию об API и добавляет схему безопасности JWT с типом Bearer.
     *
     * @return объект {@link OpenAPI} с настроенной документацией и схемой авторизации
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ManageSystem API")
                        .version("1.0")
                        .description("API для управления системой с JWT-авторизацией"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Введите JWT-токен в формате Bearer")));
    }
}