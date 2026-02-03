package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {

    private final DeliveryService service;

    @Override
    @PutMapping
    public DeliveryDto createDelivery(@RequestBody NewDeliveryRequestDto request) {
        return service.createDelivery(request);
    }

    @Override
    @PostMapping("/cost")
    public Double calculateDeliveryCost(@RequestBody OrderDto orderDto) {
        return service.calculateDeliveryCost(orderDto);
    }

    @Override
    @PostMapping("/picked")
    public DeliveryDto pickOrder(@RequestBody UUID orderId) {
        return service.pickOrder(orderId);
    }

    @Override
    @PostMapping("/successful")
    public DeliveryDto success(@RequestBody UUID orderId) {
        return service.success(orderId);
    }

    @Override
    @PostMapping("/failed")
    public DeliveryDto failed(@RequestBody UUID orderId) {
        return service.failed(orderId);
    }
}
