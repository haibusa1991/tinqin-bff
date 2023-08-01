package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartInput;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartOperation;
import com.tinqin.bff.api.operation.cart.emptyCart.EmptyCartResult;
import com.tinqin.bff.api.operation.cart.getAllCartItems.GetAllCartItemsResult;
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
public class EmptyCartOperationProcessor implements EmptyCartOperation {
    private final UserRepository userRepository;

    @Override
    public EmptyCartResult process(EmptyCartInput input) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        user.getCartItems().forEach(user::removeCartItem);
        this.userRepository.save(user);

        return new EmptyCartResult();
    }
}
