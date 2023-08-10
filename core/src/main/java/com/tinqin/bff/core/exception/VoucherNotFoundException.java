package com.tinqin.bff.core.exception;

public class VoucherNotFoundException extends RuntimeException {
    public VoucherNotFoundException(String code) {
        super(String.format("Voucher code '%s' is not valid.", code));
    }
}
