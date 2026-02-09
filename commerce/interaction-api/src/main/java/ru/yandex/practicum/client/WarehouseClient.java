package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void registerNewProduct(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductDto checkAvailability(@RequestBody ShoppingCartDto cart);

    @PostMapping("/add")
    void addQuantity(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/assembly")
    BookedProductDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void returnProduct(@RequestBody Map<UUID, Long> products);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
