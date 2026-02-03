package ru.yandex.practicum.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_booking", schema = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderBooking {

    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    UUID bookingId;

    @Column(name = "order_id", nullable = false, unique = true)
    UUID orderId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @Column(name = "total_weight", nullable = false)
    Double totalWeight;

    @Column(name = "total_volume", nullable = false)
    Double totalVolume;

    @Column(name = "fragile", nullable = false)
    Boolean fragile;

    @ElementCollection
    @CollectionTable(
            name = "order_booking_products",
            schema = "warehouse",
            joinColumns = @JoinColumn(name = "booking_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    Map<UUID, Long> products = new HashMap<>();
}
