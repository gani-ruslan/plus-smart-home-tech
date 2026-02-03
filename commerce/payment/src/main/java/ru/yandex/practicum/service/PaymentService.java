package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exceptions.PaymentNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;
    private final ShoppingStoreClient storeClient;

    public PaymentDto createPayment(OrderDto orderDto) {
        checkOrderDetails(orderDto);
        Double productCost = calculateProductCost(orderDto);
        Double tax = productCost * 0.1;
        Double delivery = orderDto.getDeliveryPrice();
        Double total = productCost + tax + delivery;

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(orderDto.getOrderId())
                .productTotal(productCost)
                .deliveryTotal(delivery)
                .feeTotal(tax)
                .totalPayment(total)
                .state(PaymentState.PENDING)
                .build();

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public Double calculateProductCost(OrderDto orderDto) {
        checkOrderDetails(orderDto);
        return orderDto.getProducts().entrySet().stream()
                .mapToDouble(entry -> storeClient.get(entry.getKey()).getPrice() * entry.getValue())
                .sum();
    }

    public Double calculateTotalCost(OrderDto orderDto) {
        checkOrderDetails(orderDto);
        Double productCost = calculateProductCost(orderDto);
        Double tax = productCost * 0.1;
        Double delivery = orderDto.getDeliveryPrice();
        return productCost + tax + delivery;
    }

    public void successPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        orderClient.paymentSuccess(payment.getOrderId());
    }

    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private void checkOrderDetails(OrderDto orderDto) {
        if (orderDto == null
                || orderDto.getProducts() == null
                || orderDto.getDeliveryPrice() == null
                || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException();
        }
    }
}
