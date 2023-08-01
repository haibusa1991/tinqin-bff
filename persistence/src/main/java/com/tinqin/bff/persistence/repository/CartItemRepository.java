package com.tinqin.bff.persistence.repository;

import com.tinqin.bff.persistence.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findCartItemByReferencedItemId(UUID referencedItemId);
}
