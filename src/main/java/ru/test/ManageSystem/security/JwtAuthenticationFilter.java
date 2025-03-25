package ru.test.ManageSystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT-аутентификации.
 * Проверяет наличие и валидность JWT-токена в заголовке запроса,
 * устанавливает аутентификацию в контексте безопасности Spring Security.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    /**
     * Конструктор фильтра с зависимостью от провайдера токенов.
     *
     * @param tokenProvider провайдер для работы с JWT-токенами
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Выполняет фильтрацию входящих запросов.
     * Извлекает JWT-токен из заголовка Authorization, проверяет его валидность
     * и устанавливает аутентификацию в контексте безопасности, если токен действителен.
     *
     * @param request     входящий HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров для дальнейшей обработки запроса
     * @throws ServletException если возникает ошибка обработки в сервлете
     * @throws IOException      если происходит ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT-токен из заголовка Authorization запроса.
     * Проверяет наличие заголовка и префикса "Bearer ", возвращает токен без префикса.
     *
     * @param request входящий HTTP-запрос
     * @return строка с JWT-токеном или {@code null}, если токен отсутствует или некорректен
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}