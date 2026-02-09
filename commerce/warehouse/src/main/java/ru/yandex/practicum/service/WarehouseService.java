package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exceptions.NoItemInCartException;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.ProductLowQuantityInWarehouseException;
import ru.yandex.practicum.exceptions.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.model.Dimensions;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.ProductOnStock;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.ProductStockRepository;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final ProductStockRepository stockRepository;
    private final OrderBookingRepository bookingRepository;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Transactional
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        if (stockRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException();
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

        stockRepository.save(newProduct);
    }

    @Transactional
    public void addQuantity(AddProductToWarehouseRequest request) {
        ProductOnStock stock = stockRepository.findById(request.getProductId())
                .orElseThrow(NoItemInCartException::new);

        Long newQuantity = stock.getQuantity() + request.getQuantity();
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
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

            ProductOnStock stock = stockRepository.findById(productId)
                    .orElseThrow(ProductNotFoundInWarehouseException::new);

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

    @Transactional
    public BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> products = request.getProducts();
        if (products == null || products.isEmpty()) {
            throw new NoItemInCartException();
        }

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductOnStock productStock = stockRepository.findById(productId)
                    .orElseThrow(ProductNotFoundInWarehouseException::new);

            if (productStock.getQuantity() < quantity) {
                throw new ProductLowQuantityInWarehouseException(Map.of(productId, quantity));
            }

            productStock.setQuantity(productStock.getQuantity() - quantity);
            stockRepository.save(productStock);

            totalWeight += productStock.getWeight() * quantity;
            totalVolume += productStock.getDimension().volume() * quantity;
            fragile = fragile || Boolean.TRUE.equals(productStock.getFragile());
        }

        OrderBooking booking = OrderBooking.builder()
                .bookingId(UUID.randomUUID())
                .orderId(request.getOrderId())
                .totalWeight(totalWeight)
                .totalVolume(totalVolume)
                .fragile(fragile)
                .products(products)
                .build();

        bookingRepository.save(booking);

        return BookedProductDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(NoOrderFoundException::new);

        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
    }

    @Transactional
    public void returnProduct(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            throw new NoItemInCartException();
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductOnStock stock = stockRepository.findById(entry.getKey())
                    .orElseThrow(ProductNotFoundInWarehouseException::new);

            stock.setQuantity(stock.getQuantity() + entry.getValue());
            stockRepository.save(stock);
        }
    }
}
