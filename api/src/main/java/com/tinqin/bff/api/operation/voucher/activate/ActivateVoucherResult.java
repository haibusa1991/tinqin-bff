package com.tinqin.bff.api.operation.voucher.activate;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class ActivateVoucherResult implements ProcessorResult {

    private UUID userId;
    private double credit;

}
