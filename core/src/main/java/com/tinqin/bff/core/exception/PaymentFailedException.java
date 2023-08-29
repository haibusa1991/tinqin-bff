package com.tinqin.bff.core.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException() {
        super("Unable to process payment.");
    }
}
