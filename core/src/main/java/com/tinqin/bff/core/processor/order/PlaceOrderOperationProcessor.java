package com.tinqin.bff.core.processor.order;

import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartInput;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderInput;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResult;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderOperation;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResultItemData;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperation;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperationInput;
import com.tinqin.bff.core.exception.NoItemsInCartException;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StoreItemNotFoundException;
import com.tinqin.bff.core.processor.cart.EmptyCartOperationProcessor;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInputCartItem;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResultSingleItem;
import com.tinqin.storage.restexport.StorageItemRestExport;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderOperationProcessor implements PlaceOrderOperation {
    private final UserRepository userRepository;
    private final StorageItemRestExport storageClient;
    private final CartItemRepository cartItemRepository;
    private final PurchaseVoucherOperation purchaseVoucher;

    @Override
    public PlaceOrderResult process(PlaceOrderInput input) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        Set<CartItem> cart = user.getCartItems();

        if (cart.isEmpty()) {
            throw new NoItemsInCartException();
        }

        this.purchaseVoucher.process(PurchaseVoucherOperationInput.builder()
                .items(cart.stream().map(CartItem::getReferencedItemId).collect(Collectors.toSet()))
                .build());

        List<PlaceOrderInputCartItem> cartItems = cart.stream()
                .map(this::maptToPlaceOrderInputCartItem)
                .toList();

        com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput restInput =
                com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput
                        .builder()
                        .userId(user.getId().toString())
                        .cartItems(cartItems)
                        .build();

        com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult placeOrderResult = com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult.builder().build();

        try {
            placeOrderResult = storageClient.placeOrder(restInput);
        } catch (FeignException e) {
            switch (e.status()) {
                case -1 -> throw new ServiceUnavailableException("storage");
            }
        }

        EmptyCartOperationProcessor.builder()
                .cartItemRepository(this.cartItemRepository)
                .userRepository(this.userRepository)
                .build()
                .process(new EmptyCartInput());

        return PlaceOrderResult.builder()
                .orderPrice(placeOrderResult.getOrderPrice())
                .items(placeOrderResult
                        .getItems()
                        .stream()
                        .map(this::mapPlaceOrderResultSingleItemToPlaceOrderResultItemData)
                        .toList())
                .user(placeOrderResult.getUser())
                .timestamp(placeOrderResult.getTimestamp())
                .build();
    }

    private PlaceOrderInputCartItem maptToPlaceOrderInputCartItem(CartItem cartItem) {
        return PlaceOrderInputCartItem.builder()
                .referencedItemId(cartItem.getReferencedItemId().toString())
                .price(cartItem.getPrice().doubleValue())
                .quantity(cartItem.getQuantity())
                .build();
    }

    private PlaceOrderResultItemData mapPlaceOrderResultSingleItemToPlaceOrderResultItemData(PlaceOrderResultSingleItem item) {
        return PlaceOrderResultItemData.builder()
                .referencedItemId(item.getReferencedItemId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
