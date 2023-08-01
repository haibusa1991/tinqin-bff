package com.tinqin.bff.api.operation.cart.addCartItem;

import com.tinqin.bff.api.base.ProcessorInput;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
public class AddCartItemInput implements ProcessorInput {

    @org.hibernate.validator.constraints.UUID
    private String referencedItemId;

    @Positive
    private Integer quantity;
}
