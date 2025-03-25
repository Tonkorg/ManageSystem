package ru.test.ManageSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.test.ManageSystem.security.JwtAuthenticationFilter;
import ru.test.ManageSystem.security.JwtTokenProvider;

import java.util.Arrays;

/**
 * Конфигурация безопасности приложения.
 * Настраивает Spring Security для использования JWT-аутентификации, CORS и шифрования паролей.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Конструктор с зависимостью от {@link JwtTokenProvider}.
     *
     * @param jwtTokenProvider провайдер для работы с JWT-токенами
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Настраивает цепочку фильтров безопасности.
     * Отключает CSRF, устанавливает stateless-сессии, настраивает правила авторизации запросов
     * и добавляет фильтр JWT-аутентификации.
     *
     * @param http объект {@link HttpSecurity} для конфигурации безопасности
     * @return объект {@link SecurityFilterChain}, представляющий настроенную цепочку фильтров
     * @throws Exception если возникает ошибка при конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/configuration/**",
                                "/v3/api-docs").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Предоставляет бин для шифрования паролей.
     * Использует алгоритм BCrypt для хэширования паролей.
     *
     * @return объект {@link PasswordEncoder} для шифрования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Предоставляет менеджер аутентификации из конфигурации Spring Security.
     *
     * @param config объект {@link AuthenticationConfiguration} для получения менеджера аутентификации
     * @return объект {@link AuthenticationManager} для управления аутентификацией
     * @throws Exception если возникает ошибка при получении менеджера
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Настраивает источник конфигурации CORS.
     * Определяет разрешённые источники, методы, заголовки и поддержку credentials.
     *
     * @return объект {@link CorsConfigurationSource} с настройками CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://example.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}