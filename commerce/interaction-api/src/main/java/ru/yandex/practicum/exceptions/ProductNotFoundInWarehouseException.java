package ru.yandex.practicum.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.errors.HttpStatusProvide;

@Getter
public class ProductNotFoundInWarehouseException extends RuntimeException implements HttpStatusProvide {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public ProductNotFoundInWarehouseException() {
        super("There is no information about the product in stock.");
    }
}
