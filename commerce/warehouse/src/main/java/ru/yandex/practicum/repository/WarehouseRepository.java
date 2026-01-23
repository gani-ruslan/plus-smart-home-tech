package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ProductOnStock;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<ProductOnStock, UUID> {
}
