package ru.test.ManageSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Обрабатывает различные исключения, возвращая стандартизированные ответы с информацией об ошибке.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Возвращает текущую временную метку в формате ISO.
     *
     * @return строка с текущей датой и временем в формате ISO_LOCAL_DATE_TIME
     */
    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Обрабатывает исключения валидации для аргументов методов.
     * Возвращает список ошибок валидации с указанием полей и сообщений.
     *
     * @param ex исключение {@link MethodArgumentNotValidException}, содержащее ошибки валидации
     * @return объект {@link ResponseEntity} с кодом 400 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation failed")
                .message(errors.toString())
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обрабатывает исключения, связанные с ненахождением ресурса.
     *
     * @param ex исключение {@link ResourceNotFoundException} с сообщением об ошибке
     * @return объект {@link ResponseEntity} с кодом 404 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обрабатывает исключения, связанные с ненахождением пользователя по email.
     *
     * @param ex исключение {@link UsernameNotFoundException}
     * @return объект {@link ResponseEntity} с кодом 404 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("Пользователь с таким email не найден")
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обрабатывает исключения, связанные с некорректными аргументами.
     *
     * @param ex исключение {@link IllegalArgumentException} с сообщением об ошибке
     * @return объект {@link ResponseEntity} с кодом 400 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обрабатывает все необработанные исключения как внутренние ошибки сервера.
     *
     * @param ex исключение {@link Exception} общего типа
     * @return объект {@link ResponseEntity} с кодом 500 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Произошла внутренняя ошибка сервера. Пожалуйста, попробуйте позже.")
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Обрабатывает исключения, связанные с попыткой создать уже существующего пользователя.
     *
     * @param ex исключение {@link UserAlreadyExistsException} с сообщением об ошибке
     * @return объект {@link ResponseEntity} с кодом 400 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обрабатывает исключения, связанные с неверными учетными данными.
     *
     * @param ex исключение {@link BadCredentialsException}
     * @return объект {@link ResponseEntity} с кодом 401 и телом {@link ErrorResponse}
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Неверный email или пароль")
                .timestamp(getTimestamp())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Обрабатывает исключения валидации для привязки данных.
     * Возвращает карту ошибок с именами полей и сообщениями.
     *
     * @param ex исключение {@link BindException}, содержащее ошибки валидации
     * @return объект {@link ResponseEntity} с кодом 400 и телом в виде карты ошибок
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}