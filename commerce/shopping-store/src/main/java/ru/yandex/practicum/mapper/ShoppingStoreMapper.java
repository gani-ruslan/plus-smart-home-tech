package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.shopping.ProductDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingStoreMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto dto);

    List<ProductDto> toDtoList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
}
