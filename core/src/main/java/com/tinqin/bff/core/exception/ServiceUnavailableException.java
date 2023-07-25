package com.tinqin.bff.core.exception;

import java.util.UUID;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName) {
        super(String.format("Service '%s' is not available.", serviceName));
    }
}
