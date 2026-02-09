package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    public DeliveryDto createDelivery(NewDeliveryRequestDto request) {
        if (request.getFromAddress() == null || request.getToAddress() == null || request.getOrderId() == null) {
            throw new NoDeliveryFoundException();
        }

        Delivery delivery = Delivery.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(request.getOrderId())
                .totalWeight(request.getTotalWeight())
                .totalVolume(request.getTotalVolume())
                .fragile(request.getFragile())
                .deliveryState(DeliveryState.CREATED)
                .fromAddress(mapper.toAddress(request.getFromAddress()))
                .toAddress(mapper.toAddress(request.getToAddress()))
                .build();

        return mapper.toDto(repository.save(delivery));
    }

    public DeliveryDto success(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);
        orderClient.delivery(orderId);
        return mapper.toDto(delivery);
    }

    public DeliveryDto failed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery);
        orderClient.deliveryFailed(orderId);
        return mapper.toDto(delivery);
    }

    public DeliveryDto pickOrder(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.shippedToDelivery(
                ShippedToDeliveryRequest.builder()
                        .orderId(orderId)
                        .deliveryId(delivery.getDeliveryId())
                        .build()
        );
        repository.save(delivery);
        orderClient.assembly(orderId);
        return mapper.toDto(delivery);
    }

    public Double calculateDeliveryCost(OrderDto order) {
        if (order == null || order.getOrderId() == null
                || order.getDeliveryVolume() == null || order.getDeliveryWeight() == null) {
            throw new NotEnoughInfoInOrderToCalculateException();
        }

        Delivery delivery = getDeliveryByOrderIdOrThrow(order.getOrderId());
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        return getDeliveryCost(order, warehouseAddress, delivery);
    }

    private double getDeliveryCost(OrderDto order, AddressDto warehouseAddress, Delivery delivery) {
        final double base = 5.0;
        double cost = base;
        String warehouseStreet = warehouseAddress.getStreet();
        boolean fragile = Boolean.TRUE.equals(order.getFragile());

        if (warehouseStreet.contains("ADDRESS_1")) {
            cost = base * 1 + base;
        } else if (warehouseStreet.contains("ADDRESS_2")) {
            cost = base * 2 + base;
        }

        if (fragile) {
            cost += cost * 0.2;
        }

        cost += order.getDeliveryWeight() * 0.3;
        cost += order.getDeliveryVolume() * 0.2;

        String destinationStreet = delivery.getToAddress().getStreet();
        if (!warehouseStreet.equalsIgnoreCase(destinationStreet)) {
            cost += cost * 0.2;
        }

        return Math.round(cost * 100.0) / 100.0;
    }

    private Delivery getDeliveryByOrderIdOrThrow(UUID orderId) {
        if (orderId == null) {
            throw new NoOrderFoundException();
        }

        return repository.findByOrderId(orderId).orElseThrow(NoDeliveryFoundException::new);
    }
}
