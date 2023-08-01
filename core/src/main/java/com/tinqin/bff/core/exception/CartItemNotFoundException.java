package com.tinqin.bff.core.exception;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(UUID id) {
        super(String.format("No item with id '%s' exists in cart.", id));
    }
}
