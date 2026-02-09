package ru.yandex.practicum.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.errors.HttpStatusProvide;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException implements HttpStatusProvide {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public SpecifiedProductAlreadyInWarehouseException() {
        super("The product with the specified identifier is already registered in the warehouse.");
    }
}
