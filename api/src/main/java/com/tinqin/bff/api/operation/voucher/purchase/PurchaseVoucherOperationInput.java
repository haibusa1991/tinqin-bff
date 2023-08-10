package com.tinqin.bff.api.operation.voucher.purchase;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder

public class PurchaseVoucherOperationInput implements ProcessorInput {
    private Set<UUID> items;
}