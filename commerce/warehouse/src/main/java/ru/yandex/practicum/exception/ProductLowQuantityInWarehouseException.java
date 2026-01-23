package ru.yandex.practicum.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.errors.HttpStatusProvide;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ProductLowQuantityInWarehouseException extends RuntimeException implements HttpStatusProvide {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final Map<UUID, Long> missingProducts;

    public ProductLowQuantityInWarehouseException(Map<UUID, Long> missingProducts) {
        super(buildMessage(missingProducts));
        this.missingProducts = missingProducts;
    }

    private static String buildMessage(Map<UUID, Long> missingProducts) {
        if (missingProducts == null || missingProducts.isEmpty()) {
            return "The required quantity of products are not in stock.";
        }

        return missingProducts.entrySet().stream()
                .map(e -> String.format("Product %s - %d units missing.", e.getKey(), e.getValue()))
                .collect(Collectors.joining());
    }
}
