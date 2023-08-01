package com.tinqin.bff.api.operation.cart.removeCartItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqin.bff.api.base.ProcessorInput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class RemoveCartItemInput implements ProcessorInput {

    @JsonIgnore
    private UUID referencedItemId;
}
