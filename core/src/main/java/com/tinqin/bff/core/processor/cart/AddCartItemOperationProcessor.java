package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemInput;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemOperation;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemResult;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StoreItemNotFoundException;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
import com.tinqin.storage.restexport.StorageRestExport;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddCartItemOperationProcessor implements AddCartItemOperation {
    private final StorageRestExport storageClient;
    private final ZooStoreRestExport storeClient;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Override
    public AddCartItemResult process(AddCartItemInput input) {
        UUID referencedItem = UUID.fromString(input.getReferencedItemId());

        try {
            this.storeClient.getItemById(input.getReferencedItemId());
        } catch (FeignException e) {
            switch (e.status()) {
                case -1 -> throw new ServiceUnavailableException("zoostore");
                case 404 -> throw new StoreItemNotFoundException(referencedItem);
            }
        }

        //TODO fix instantiation with new
        GetStorageItemByReferencedIdResult storage = new GetStorageItemByReferencedIdResult();
        try {
            storage = this.storageClient.getItemByReferencedItemId(Set.of(referencedItem.toString()));
        } catch (FeignException e) {
            switch (e.status()) {
                case -1 -> throw new ServiceUnavailableException("storage");
                case 404 -> throw new StoreItemNotFoundException(referencedItem);
            }
        }

        GetStorageItemByReferenceIdSingleItem storageItem = storage.getItems()
                .stream()
                .findFirst()
                .orElseThrow(() -> new StoreItemNotFoundException(referencedItem));


        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        Optional<CartItem> existingCartItemOptional = this.cartItemRepository
                .findCartItemByReferencedItemId(UUID.fromString(input.getReferencedItemId()));

        CartItem cartItem  = CartItem.builder()
                    .referencedItemId(referencedItem)
                    .quantity(input.getQuantity())
                    .price(BigDecimal.valueOf(storageItem.getPrice()))
                    .user(user)
                    .build();

        if (existingCartItemOptional.isPresent()) {
            cartItem = existingCartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + input.getQuantity());
        }

        CartItem persistedCartItem = this.cartItemRepository.save(cartItem);

        user.addCartItem(persistedCartItem);
        User persistedUser = this.userRepository.save(user);

        return new AddCartItemResult(
                persistedUser.getCartItems()
                        .stream()
                        .map(this::serializeCartItem)
                        .collect(Collectors.toSet())
        );
    }

    private String serializeCartItem(CartItem cartItem) {
        return String.join("|", cartItem.getReferencedItemId().toString(),
                cartItem.getQuantity().toString(),
                String.format(Locale.ROOT,"%.2f", cartItem.getPrice().doubleValue()).formatted());
    }
}
