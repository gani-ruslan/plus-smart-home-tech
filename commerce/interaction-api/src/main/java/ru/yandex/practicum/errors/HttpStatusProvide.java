package ru.yandex.practicum.errors;

import org.springframework.http.HttpStatus;

public interface HttpStatusProvide {
    HttpStatus getStatus();
}
