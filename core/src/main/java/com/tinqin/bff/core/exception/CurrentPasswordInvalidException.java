package com.tinqin.bff.core.exception;

public class CurrentPasswordInvalidException extends RuntimeException {
    public CurrentPasswordInvalidException() {
        super("Old password is invalid.");
    }
}
