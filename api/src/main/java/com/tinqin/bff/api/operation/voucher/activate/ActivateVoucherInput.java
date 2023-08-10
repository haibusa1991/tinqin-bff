package com.tinqin.bff.api.operation.voucher.activate;


import com.tinqin.bff.api.base.ProcessorInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter(AccessLevel.PRIVATE)
@Getter
public class ActivateVoucherInput implements ProcessorInput {
    private String voucherCode;
}