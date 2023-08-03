package com.tinqin.bff.api.operation.order.placeOrder;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder

public class PlaceOrderInput implements ProcessorInput {
    @JsonIgnore
    private UUID referencedItemId;
}