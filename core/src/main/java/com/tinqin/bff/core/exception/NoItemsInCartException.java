package com.tinqin.bff.core.exception;

import java.util.UUID;

public class NoItemsInCartException extends RuntimeException {
    public NoItemsInCartException() {
        super("No items to purchase.");
    }
}
