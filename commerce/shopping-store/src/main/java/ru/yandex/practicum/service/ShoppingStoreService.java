package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exceptions.ProductNotFoundException;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.shopping.ProductDto;
import ru.yandex.practicum.shopping.SetProductQuantityStateRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;
    private final ShoppingStoreMapper mapper;

    public Page<ProductDto> findByCategory(ProductCategory category, Pageable pageable) {
        return repository.findByProductCategory(category, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        Product product = mapper.toEntity(dto);
        if (product.getProductId() == null) product.setProductId(UUID.randomUUID());
        if (product.getProductState() == null) product.setProductState(ProductState.ACTIVE);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public ProductDto update(ProductDto dto) {
        Product product = repository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(dto.getProductId()));

        mapper.updateEntityFromDto(dto, product);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public boolean removeFromStore(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Transactional
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        return true;
    }

    public ProductDto getById(UUID id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
