package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.exception.ProductLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.ProductAlreadyInWarehouseException;
import ru.yandex.practicum.model.Dimensions;
import ru.yandex.practicum.model.ProductOnStock;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository repository;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Transactional
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            throw new ProductAlreadyInWarehouseException();
        }

        ProductOnStock newProduct = ProductOnStock.builder()
                .productId(request.getProductId())
                .fragile(request.getFragile())
                .dimension(new Dimensions(
                        request.getDimension().getWidth(),
                        request.getDimension().getHeight(),
                        request.getDimension().getDepth()
                ))
                .weight(request.getWeight())
                .quantity(0L)
                .build();

        repository.save(newProduct);
    }

    @Transactional
    public void addQuantity(AddProductToWarehouseRequest request) {
        ProductOnStock stock = repository.findById(request.getProductId()).orElseThrow(NoClassDefFoundError::new);

        Long newQuantity = stock.getQuantity() + request.getQuantity();
        stock.setQuantity(newQuantity);
        repository.save(stock);
    }

    @Transactional(readOnly = true)
    public BookedProductDto checkAvailability(ShoppingCartDto cart) {
        Map<UUID, Long> items = cart.getProducts();
        if (items == null || items.isEmpty()) {
            return BookedProductDto.builder()
                    .deliveryWeight(0.0)
                    .deliveryVolume(0.0)
                    .fragile(false)
                    .build();
        }

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean anyFragile = false;

        Map<UUID, Long> missingProducts = new HashMap<>();

        for (Map.Entry<UUID, Long> entry : items.entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue() == null ? 0 : entry.getValue();

            ProductOnStock stock = repository.findById(productId).orElseThrow(ProductNotFoundInWarehouseException::new);

            if (stock.getQuantity() < requestedQuantity) {
                long missing = requestedQuantity - stock.getQuantity();
                missingProducts.put(productId, missing);
            } else {
                Double itemVolume = stock.getDimension().volume();
                totalVolume += itemVolume * requestedQuantity;
                totalWeight += stock.getWeight() * requestedQuantity;
                anyFragile = anyFragile || Boolean.TRUE.equals(stock.getFragile());
            }
            if (!missingProducts.isEmpty()) {
                throw new ProductLowQuantityInWarehouseException(missingProducts);
            }
        }

        return BookedProductDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(anyFragile)
                .build();
    }

    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
