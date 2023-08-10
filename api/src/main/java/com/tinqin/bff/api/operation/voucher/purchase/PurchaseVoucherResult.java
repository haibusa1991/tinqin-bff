package com.tinqin.bff.api.operation.voucher.purchase;

import com.tinqin.bff.api.base.ProcessorResult;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResultItemData;
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
public class PurchaseVoucherResult implements ProcessorResult {

}
