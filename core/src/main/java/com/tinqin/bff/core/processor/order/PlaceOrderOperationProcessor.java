package com.tinqin.bff.core.processor.order;

import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderInput;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResult;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderOperation;
import com.tinqin.bff.core.exception.NoItemsInCartException;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInputCartItem;
import com.tinqin.storage.restexport.StorageItemRestExport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceOrderOperationProcessor implements PlaceOrderOperation {
    private final UserRepository userRepository;
    private final StorageItemRestExport storageClient;

    @Override
    public PlaceOrderResult process(PlaceOrderInput input) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        Set<CartItem> cart = user.getCartItems();

        if (cart.isEmpty()) {
            throw new NoItemsInCartException();
        }

        List<PlaceOrderInputCartItem> cartItems = cart.stream()
                .map(this::maptToPlaceOrderInputCartItem)
                .toList();

        com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput restInput =
                com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput
                        .builder()
                        .userId(user.getId().toString())
                        .cartItems(cartItems)
                        .build();

        com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult placeOrderResult =
                storageClient.placeOrder(restInput);

        return null;
    }

    private PlaceOrderInputCartItem maptToPlaceOrderInputCartItem(CartItem cartItem) {
        return PlaceOrderInputCartItem.builder()
                .referencedItemId(cartItem.getReferencedItemId().toString())
                .price(cartItem.getPrice().doubleValue())
                .quantity(cartItem.getQuantity())
                .build();
    }

}
