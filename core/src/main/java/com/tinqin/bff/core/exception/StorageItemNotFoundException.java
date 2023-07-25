package com.tinqin.bff.core.exception;

import java.util.UUID;

public class StorageItemNotFoundException extends RuntimeException {
    public StorageItemNotFoundException(UUID id) {
        super(String.format("No item with id '%s' found in storage.", id));
    }
}
