package com.tinqin.bff.core.processor.order;

import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartInput;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartOperation;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderInput;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResult;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderOperation;
import com.tinqin.bff.api.operation.order.placeOrder.PlaceOrderResultItemData;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperation;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperationInput;
import com.tinqin.bff.core.exception.NoItemsInCartException;
import com.tinqin.bff.core.exception.PaymentFailedException;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StoreItemNotFoundException;
import com.tinqin.bff.core.processor.cart.EmptyCartOperationProcessor;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.payments.api.operation.payment.payment.PaymentOperationInput;
import com.tinqin.payments.api.operation.payment.payment.PaymentOperationResult;
import com.tinqin.payments.restexport.PaymentsRestExport;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInputCartItem;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResultSingleItem;
import com.tinqin.storage.restexport.StorageRestExport;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceOrderOperationProcessor implements PlaceOrderOperation {
    private final UserRepository userRepository;
    private final StorageRestExport storageClient;
    private final CartItemRepository cartItemRepository;
    private final PurchaseVoucherOperation purchaseVoucher;
    private final EmptyCartOperation emptyCart;
    private final PaymentsRestExport paymentClient;

    @Override
    public PlaceOrderResult process(PlaceOrderInput input) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        Set<CartItem> cart = user.getCartItems();

        if (cart.isEmpty()) {
            throw new NoItemsInCartException();
        }

        PaymentOperationInput paymentData = PaymentOperationInput.builder()
                .cardNumber(input.getCardNumber())
                .expiryMonth(input.getExpiryMonth())
                .expiryYear(input.getExpiryYear())
                .cvc(input.getCvc())
                .cartPrice(cart.stream().map(CartItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue())
                .build();

        PaymentOperationResult paymentOperationResult = paymentClient.makeCharge(paymentData);

        if(!paymentOperationResult.isSuccessful()){
            throw new PaymentFailedException();
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
                        .userCredit(user.getCredit().doubleValue())
                        .userId(user.getId().toString())
                        .cartItems(cartItems)
                        .build();

        com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult placeOrderResult =
                com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult.builder().build();

        try {
            placeOrderResult = storageClient.placeOrder(restInput);
        } catch (FeignException e) {
            switch (e.status()) {
                case -1 -> throw new ServiceUnavailableException("storage");
            }
        }
        user.setCredit(BigDecimal.valueOf(placeOrderResult.getRemainingUserCredit()));
        this.userRepository.save(user);

        this.emptyCart.process(new EmptyCartInput());

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
