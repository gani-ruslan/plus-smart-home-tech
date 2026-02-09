package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService service;

    @Override
    @PutMapping
    public void registerNewProduct(@Valid @RequestBody NewProductInWarehouseRequest request) {
        service.registerNewProduct(request);
    }

    @Override
    @PostMapping("/add")
    public void addQuantity(@Valid @RequestBody AddProductToWarehouseRequest request) {
        service.addQuantity(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductDto checkAvailability(@Valid @RequestBody ShoppingCartDto cart) {
        return service.checkAvailability(cart);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductDto assemblyProductsForOrder(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        service.shippedToDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void returnProduct(@Valid @RequestBody Map<UUID, Long> products) {
        service.returnProduct(products);
    }
}
