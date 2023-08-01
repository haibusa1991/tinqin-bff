package com.tinqin.bff.persistence.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@Getter
@Setter
public class CartItem {

    @Builder
    public CartItem(UUID referencedItemId, Integer quantity, BigDecimal price) {
        this.referencedItemId = referencedItemId;
        this.quantity = quantity;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID referencedItemId;

    private Integer quantity;

    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;

        return referencedItemId.equals(cartItem.referencedItemId);
    }

    @Override
    public int hashCode() {
        return referencedItemId.hashCode();
    }
}
