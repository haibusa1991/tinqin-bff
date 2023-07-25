package com.tinqin.bff.core.exception;

import java.util.UUID;

public class StoreItemNotFoundException extends RuntimeException {
    public StoreItemNotFoundException(UUID id) {
        super(String.format("No item with id '%s' found in store.", id));
    }
}
