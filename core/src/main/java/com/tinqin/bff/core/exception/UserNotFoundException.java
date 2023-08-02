package com.tinqin.bff.core.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super(String.format("User with email '%s' does not exist.", email));
    }
}
