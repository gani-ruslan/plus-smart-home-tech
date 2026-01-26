package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.shopping.ProductDto;
import ru.yandex.practicum.shopping.SetProductQuantityStateRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreService service;

    @GetMapping
    @Override
    public Page<ProductDto> getByCategory(@RequestParam("category") ProductCategory category, Pageable pageable) {
        return service.findByCategory(category, pageable);
    }

    @Override
    @PutMapping
    public ProductDto create(@Valid @RequestBody ProductDto dto) {
        return service.create(dto);
    }

    @Override
    @PostMapping
    public ProductDto update(@Valid @RequestBody ProductDto dto) {
        return service.update(dto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public boolean remove(@RequestBody UUID productId) {
        return service.removeFromStore(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public boolean updateQuantity(SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto get(@PathVariable UUID productId) {
        return service.getById(productId);
    }
}
