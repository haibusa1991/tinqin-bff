package com.tinqin.bff.core.exception;

public class VoucherExpiredException extends RuntimeException {
    public VoucherExpiredException(String code) {
        super(String.format("Voucher with code '%s' has expired.", code));
    }
}
