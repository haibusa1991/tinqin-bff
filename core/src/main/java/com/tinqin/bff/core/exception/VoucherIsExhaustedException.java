package com.tinqin.bff.core.exception;

import java.util.UUID;

public class VoucherIsExhaustedException extends RuntimeException {
    public VoucherIsExhaustedException(String code) {
        super(String.format("Voucher with code '%s' is already used.", code));
    }
}
