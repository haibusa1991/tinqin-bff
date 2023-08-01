package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemResult;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsInput;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsOperation;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsResult;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllCartItemsOperationProcessor implements GetAllCartItemsOperation {
    private final UserRepository userRepository;

    @Override
    public GetAllCartItemsResult process(GetAllCartItemsInput input) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        return new GetAllCartItemsResult(
                user.getCartItems()
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
