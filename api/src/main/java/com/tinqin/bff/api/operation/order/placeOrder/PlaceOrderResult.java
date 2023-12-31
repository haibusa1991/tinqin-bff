package com.tinqin.bff.api.operation.order.placeOrder;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class PlaceOrderResult implements ProcessorResult {
    private List<PlaceOrderResultItemData> items;
    private LocalDateTime timestamp;
    private UUID user;
    private Double orderPrice;
}
