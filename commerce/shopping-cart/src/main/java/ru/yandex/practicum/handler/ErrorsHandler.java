package ru.yandex.practicum.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.errors.ApiErrors;
import ru.yandex.practicum.errors.HttpStatusProvide;
import ru.yandex.practicum.exceptions.AuthorizationUserException;
import ru.yandex.practicum.exceptions.CartNotFoundException;
import ru.yandex.practicum.exceptions.NoItemInCartException;

@RestControllerAdvice
public class ErrorsHandler {

    @ExceptionHandler({
            AuthorizationUserException.class,
            NoItemInCartException.class,
            CartNotFoundException.class
    })
    public ResponseEntity<ApiErrors> handle(RuntimeException exception) {
        HttpStatusProvide statusProvide = (HttpStatusProvide) exception;

        ApiErrors error = ApiErrors.builder()
                .cause(exception.getCause())
                .stackTrace(exception.getStackTrace())
                .httpStatus(statusProvide.getStatus())
                .userMessage(exception.getMessage())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(statusProvide.getStatus()).body(error);
    }
}
