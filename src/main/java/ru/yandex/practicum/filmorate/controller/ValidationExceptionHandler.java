package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@Slf4j
public class ValidationExceptionHandler {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        log.error("Валидация не прошла по запросу: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            log.warn("Ошибка данных: {} = '{}' | Причина: {}",
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage()
            );
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return Map.of("Ошибки", errors, "Время", LocalDateTime.now().format(formatter));
    }
}
