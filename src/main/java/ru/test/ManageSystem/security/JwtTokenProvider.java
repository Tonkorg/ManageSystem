package ru.test.ManageSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Провайдер для работы с JWT-токенами.
 * Отвечает за генерацию, валидацию и извлечение данных из токенов,
 * а также создание объекта аутентификации для Spring Security.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:your-256-bit-secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long validityInMilliseconds;

    /**
     * Инициализирует секретный ключ, преобразуя его в Base64-формат.
     * Вызывается автоматически после создания бина.
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Генерирует JWT-токен на основе данных аутентификации.
     * Включает имя пользователя и роли в токен, устанавливает время создания и истечения.
     *
     * @param authentication объект {@link Authentication} с данными аутентифицированного пользователя
     * @return строка с сгенерированным JWT-токеном
     */
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Извлекает имя пользователя (email) из JWT-токена.
     *
     * @param token строка с JWT-токеном
     * @return имя пользователя (email), указанное в токене
     * @throws JwtException если токен некорректен или не может быть распарсен
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяет валидность JWT-токена.
     * Возвращает true, если токен действителен и подписан правильным ключом.
     *
     * @param token строка с JWT-токеном
     * @return {@code true}, если токен валиден, иначе {@code false}
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Создаёт объект аутентификации на основе данных из JWT-токена.
     * Извлекает имя пользователя и роли, формирует объект {@link UserDetailsImpl}.
     *
     * @param token строка с JWT-токеном
     * @return объект {@link Authentication} для использования в Spring Security
     * @throws JwtException если токен некорректен или не может быть распарсен
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .email(claims.getSubject())
                .password("")
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }
}