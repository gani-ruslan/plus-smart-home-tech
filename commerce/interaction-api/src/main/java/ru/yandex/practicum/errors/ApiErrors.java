package ru.yandex.practicum.errors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiErrors {
    Throwable cause;
    StackTraceElement[] stackTrace;
    HttpStatus httpStatus;
    String userMessage;
    String message;
    Throwable[] suppressed;
    String localizedMessage;
}
