package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsResult;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemInput;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemOperation;
import com.tinqin.bff.api.operation.cart.removeCartItem.RemoveCartItemResult;
import com.tinqin.bff.core.exception.CartItemNotFoundException;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemoveCartItemOperationService implements RemoveCartItemOperation {
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public RemoveCartItemResult process(RemoveCartItemInput input) {

        CartItem cartItem = this.cartItemRepository
                .findCartItemByReferencedItemId(input.getReferencedItemId())
                .orElseThrow(() -> new CartItemNotFoundException(input.getReferencedItemId()));

        this.cartItemRepository.delete(cartItem);

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        user.removeCartItem(cartItem);
        User persisted = this.userRepository.save(user);

        return new RemoveCartItemResult(
                persisted.getCartItems()
                        .stream()
                        .map(this::serializeCartItem)
                        .collect(Collectors.toSet())
        );
    }

    private String serializeCartItem(CartItem cartItem) {

        return String.join("|", cartItem.getReferencedItemId().toString(),
                cartItem.getQuantity().toString(),
                cartItem.getPrice().toString());
    }
}
