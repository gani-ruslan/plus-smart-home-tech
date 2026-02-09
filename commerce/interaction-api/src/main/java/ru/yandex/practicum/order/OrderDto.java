package ru.yandex.practicum.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

    UUID orderId;
    UUID shoppingCartId;

    @NotNull
    Map<UUID, Long> products;

    UUID paymentId;
    UUID deliveryId;

    String state;

    Double deliveryWeight;
    Double deliveryVolume;

    Boolean fragile;

    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}
