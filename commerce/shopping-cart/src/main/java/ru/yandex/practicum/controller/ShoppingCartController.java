package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.service.ShoppingCartService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService service;

    @Override
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return service.getCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String username,
                                       @RequestBody Map<UUID, Long> products) {
        return service.addProducts(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivate(@RequestParam String username) {
        service.deactivate(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username,
                                          @RequestBody List<UUID> productIds) {
        return service.removeProducts(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @Valid @RequestBody ChangeProductQuantityRequest request) {
        return service.changeQuantity(username, request);
    }
}
