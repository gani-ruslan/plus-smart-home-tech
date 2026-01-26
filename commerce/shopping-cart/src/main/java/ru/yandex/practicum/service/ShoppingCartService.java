package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.exception.NoItemInCartException;
import ru.yandex.practicum.exception.AuthorizationUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Transactional
    public ShoppingCartDto getCart(String username) {
        isUsernameValid(username);
        ShoppingCart cart = repository.findFirstByUsernameAndStateOrderByCreatedAtDesc(username, ShoppingCart.CartState.ACTIVE)
                .or(() -> repository.findFirstByUsernameOrderByCreatedAtDesc(username))
                .orElseGet(() -> createNewCart(username));
        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        isUsernameValid(username);
        ShoppingCart cart = getOrCreateActiveCart(username);

        products.forEach((productId, quantity) -> cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.setQuantity(i.getQuantity() + quantity),
                        () -> cart.getItems().add(Item.builder()
                                .id(UUID.randomUUID())
                                .cart(cart)
                                .productId(productId)
                                .quantity(quantity)
                                .build())
                ));

        repository.save(cart);
        ShoppingCartDto dto = mapper.toDto(cart);
        warehouseClient.checkAvailability(dto);

        return dto;
    }

    @Transactional
    public void deactivate(String username) {
        isUsernameValid(username);
        repository.findFirstByUsernameAndStateOrderByCreatedAtDesc(username, ShoppingCart.CartState.ACTIVE)
                .ifPresent(cart -> {
                    cart.setState(ShoppingCart.CartState.DEACTIVATED);
                    repository.save(cart);
                });
    }

    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> productsIds) {
        isUsernameValid(username);
        ShoppingCart cart = repository.findFirstByUsernameAndStateOrderByCreatedAtDesc(username, ShoppingCart.CartState.ACTIVE)
                .orElseThrow(NoItemInCartException::new);

        if (!cart.getItems().removeIf(i -> productsIds.contains(i.getProductId()))) {
            throw new NoItemInCartException();
        }

        repository.save(cart);
        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        isUsernameValid(username);

        ShoppingCart cart = repository.findFirstByUsernameAndStateOrderByCreatedAtDesc(username, ShoppingCart.CartState.ACTIVE)
                .orElseThrow(NoItemInCartException::new);

        Item item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(NoItemInCartException::new);

        item.setQuantity(request.getNewQuantity());
        repository.save(cart);
        ShoppingCartDto dto = mapper.toDto(cart);
        warehouseClient.checkAvailability(dto);

        return dto;
    }

    private void isUsernameValid(String username) {
        if (username == null || username.isBlank()) {
            throw new AuthorizationUserException("Username must be provided.");
        }
    }

    private ShoppingCart getOrCreateActiveCart(String username) {
        return repository.findFirstByUsernameAndStateOrderByCreatedAtDesc(username, ShoppingCart.CartState.ACTIVE)
                .orElseGet(() -> createNewCart(username));
    }

    private ShoppingCart createNewCart(String username) {
        return repository.save(ShoppingCart.builder()
                .shoppingCartId(UUID.randomUUID())
                .username(username)
                .state(ShoppingCart.CartState.ACTIVE)
                .createdAt(Instant.now())
                .build());
    }
}
