package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemInput;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemOperation;
import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemResult;
import com.tinqin.bff.core.exception.InsufficientItemQuantityException;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StoreItemNotFoundException;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
import com.tinqin.storage.restexport.StorageItemRestExport;
import com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddCartItemOperationProcessor implements AddCartItemOperation {
    private final StorageItemRestExport storageClient;
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


        int quantityDifference = storageItem.getQuantity() - input.getQuantity();
        if (quantityDifference < 0) {
            throw new InsufficientItemQuantityException(referencedItem, input.getQuantity(), storageItem.getQuantity());
        }

        CartItem cartItem = this.cartItemRepository.save(
                CartItem.builder()
                        .referencedItemId(referencedItem)
                        .quantity(input.getQuantity())
                        .price(BigDecimal.valueOf(storageItem.getPrice()))
                        .build()
        );

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not valid"));

        user.addCartItem(cartItem);
        User persisted = this.userRepository.save(user);

        return new AddCartItemResult(
                persisted.getCartItems()
                        .stream()
                        .map(this::serializeCartItem)
                        .collect(Collectors.toSet())
        );
    }

    private String serializeCartItem(CartItem cartItem){

       return String.join("|", cartItem.getReferencedItemId().toString(),
                cartItem.getQuantity().toString(),
                cartItem.getPrice().toString());
    }
}
