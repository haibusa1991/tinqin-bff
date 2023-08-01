package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemInput;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemOperation;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemResult;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartInput;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartOperation;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartResult;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsInput;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsOperation;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsResult;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemInput;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemOperation;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/cart")
public class CartController {
    private final AddCartItemOperation addCartItem;
    private final EmptyCartOperation emptyCart;
    private final GetAllCartItemsOperation getAllCartItems;
    private final RemoveCartItemOperation removeCartItem;


    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<AddCartItemResult> addCartItem(@RequestBody @Valid AddCartItemInput input) {
        return ResponseEntity.ok(this.addCartItem.process(input));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<GetAllCartItemsResult> getCartContents() {
        return ResponseEntity.ok(this.getAllCartItems.process(new GetAllCartItemsInput()));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping
    public ResponseEntity<EmptyCartResult> emptyCartContents() {
        return new ResponseEntity<>(this.emptyCart.process(new EmptyCartInput()), HttpStatus.NO_CONTENT);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(path = "/{referencedItemId}")
    public ResponseEntity<RemoveCartItemResult> removeCartItem(@PathVariable @org.hibernate.validator.constraints.UUID String referencedItemId) {
        return ResponseEntity.ok(this.removeCartItem.process(new RemoveCartItemInput(UUID.fromString(referencedItemId))));
    }
}
