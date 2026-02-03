package ru.yandex.practicum.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.errors.HttpStatusProvide;

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException implements HttpStatusProvide {

    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public NotEnoughInfoInOrderToCalculateException() {
        super("Insufficient information in the order for calculation.");
    }
}
