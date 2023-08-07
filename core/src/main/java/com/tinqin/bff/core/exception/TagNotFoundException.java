package com.tinqin.bff.core.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(UUID tagId) {
        super(String.format("No tag with id '%s' exists", tagId));
    }
}
