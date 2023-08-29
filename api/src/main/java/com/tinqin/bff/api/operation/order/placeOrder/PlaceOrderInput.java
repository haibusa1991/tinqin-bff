package com.tinqin.bff.api.operation.order.placeOrder;


import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder

public class PlaceOrderInput implements ProcessorInput {
    private String cardNumber;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cvc;
}