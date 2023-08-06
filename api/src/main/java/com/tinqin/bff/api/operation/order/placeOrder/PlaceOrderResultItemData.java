package com.tinqin.bff.api.operation.order.placeOrder;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class PlaceOrderResultItemData implements ProcessorResult {
    private UUID referencedItemId;
    private Double price;
    private Integer quantity;
}
