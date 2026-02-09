package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.warehouse.AddressDto;

@Component
public class DeliveryMapper {

    public DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .fromAddress(toAddressDto(delivery.getFromAddress()))
                .toAddress(toAddressDto(delivery.getToAddress()))
                .deliveryState(delivery.getDeliveryState())
                .build();
    }

    public Delivery toEntity(DeliveryDto dto) {
        if (dto == null) return null;
        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState() != null ? dto.getDeliveryState() : DeliveryState.CREATED)
                .fromAddress(toAddress(dto.getFromAddress()))
                .toAddress(toAddress(dto.getToAddress()))
                .build();
    }

    public AddressDto toAddressDto(Address address) {
        if (address == null) return null;
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    public Address toAddress(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .addressId(java.util.UUID.randomUUID())
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }
}
