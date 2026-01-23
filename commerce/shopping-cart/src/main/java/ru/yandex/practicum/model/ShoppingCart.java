package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "carts", schema = "shopping_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {

    @Id
    @Column(name = "shopping_cart_id", nullable = false, updatable = false)
    UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    CartState state;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    Set<Item> items = new HashSet<>();

    public enum CartState {
        ACTIVE,
        DEACTIVATED
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
