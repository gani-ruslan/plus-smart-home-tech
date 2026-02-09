package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.PaymentState;

import java.util.UUID;

@Entity
@Table(name = "payments", schema = "payment_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    UUID paymentId;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Column(name = "product_total")
    Double productTotal;

    @Column(name = "delivery_total")
    Double deliveryTotal;

    @Column(name = "fee_total")
    Double feeTotal;

    @Column(name = "total_payment")
    Double totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    PaymentState state;
}
