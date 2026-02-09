package ru.yandex.practicum.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.errors.HttpStatusProvide;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException implements HttpStatusProvide {

    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public ProductNotFoundException(UUID id) {
        super("Product with ID:" + id + " not found.");
    }
}
