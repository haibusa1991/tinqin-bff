package com.tinqin.bff.api.operation.cart.addCartItem;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AddCartItemResult implements ProcessorResult {
    private final Set<String> cartContents;
}

