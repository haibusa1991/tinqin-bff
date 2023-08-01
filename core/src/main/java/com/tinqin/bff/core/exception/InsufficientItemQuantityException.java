package com.tinqin.bff.core.exception;

import java.util.UUID;

public class InsufficientItemQuantityException extends RuntimeException {
    public InsufficientItemQuantityException(UUID referencedItemId, Integer required, Integer available) {
        super(String.format("Insufficient quantity of item '%s'. Required - %d, available - %d", referencedItemId, required, available));
    }
}
