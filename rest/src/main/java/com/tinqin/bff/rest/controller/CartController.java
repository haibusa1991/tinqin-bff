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
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderInput;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderOperation;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResult;
import com.tinqin.bff.core.processor.order.PlaceOrderOperationProcessor;
import com.tinqin.restexport.annotation.RestExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final PlaceOrderOperation placeOrder;


    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @RestExport
    @ApiResponse(responseCode = "200", description = "Item added successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid field contents.")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @ApiResponse(responseCode = "404", description = "Specified item does not exist.")
    @ApiResponse(responseCode = "503", description = "Zoostore or Storage service is not available")
    @Operation(description = "Adds the specified quantity of the specified item to the cart of the current user.",
            summary = "Adds item to current user's cart.")
    public ResponseEntity<AddCartItemResult> addCartItem(@RequestBody @Valid AddCartItemInput input) {
        return ResponseEntity.ok(this.addCartItem.process(input));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Returns cart contents.")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @Operation(description = "Gets the cart content for the current user.",
            summary = "Gets current cart content.")
    public ResponseEntity<GetAllCartItemsResult> getCartContents() {
        return ResponseEntity.ok(this.getAllCartItems.process(new GetAllCartItemsInput()));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @Operation(description = "Removes all cart content for the current user.",
            summary = "Removes all cart content.")
    public ResponseEntity<EmptyCartResult> emptyCartContents() {
        this.emptyCart.process(new EmptyCartInput());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Item successfully removed from cart.")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @ApiResponse(responseCode = "404", description = "Specified item not found in cart.")
    @ApiResponse(responseCode = "409", description = "Invalid field contents.")
    @Operation(description = "Removes the specified item from the cart for the current user.",
            summary = "Removes item from cart.")
    @DeleteMapping(path = "/{referencedItemId}")
    public ResponseEntity<RemoveCartItemResult> removeCartItem(@PathVariable @org.hibernate.validator.constraints.UUID String referencedItemId) {
        return ResponseEntity.ok(this.removeCartItem.process(new RemoveCartItemInput(UUID.fromString(referencedItemId))));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Items successfully ordered.")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @ApiResponse(responseCode = "404", description = "Cart is empty.")
    @ApiResponse(responseCode = "503", description = "Zoostore or Storage service is not available")
    @Operation(description = "Puts order towards the storage services and empties the cart.",
            summary = "Orders all items in cart.")
    @PostMapping(path = "/place-order")
    public ResponseEntity<PlaceOrderResult> placeOrder(PlaceOrderInput input) {
        return ResponseEntity.ok(this.placeOrder.process(input));
    }
}
