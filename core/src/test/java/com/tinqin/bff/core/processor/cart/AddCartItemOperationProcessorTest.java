package com.tinqin.bff.core.processor.cart;

import com.tinqin.bff.api.operation.cart.addCartItem.AddCartItemInput;
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
import com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AddCartItemOperationProcessorTest {

    @MockBean
    private StorageRestExport storageClient;

    @MockBean
    private ZooStoreRestExport storeClient;

    @MockBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    SecurityContextHolder securityContextHolder;


    AddCartItemOperationProcessor processor;
    AddCartItemInput input;
    CartItem existingCartItem;
    com.tinqin.bff.persistence.entity.User user = mock(User.class);
    GetStorageItemByReferencedIdResult getStorageItemByReferencedIdResult;
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    UserDetails userDetails = mock(UserDetails.class);

    @BeforeEach
    public void setUp() {
        processor = new AddCartItemOperationProcessor(storageClient,
                storeClient,
                cartItemRepository,
                userRepository);

        input = AddCartItemInput.builder()
                .referencedItemId("00000000-0000-0000-0000-000000000000")
                .quantity(10)
                .build();

        user = User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-111111111111"))
                .cartItems(new HashSet<>())
                .build();

        existingCartItem = CartItem.builder()
                .referencedItemId(UUID.fromString(input.getReferencedItemId()))
                .quantity(1000)
                .price(BigDecimal.valueOf(10.0))
                .user(user)
                .build();


        List<GetStorageItemByReferenceIdSingleItem> items = List.of(GetStorageItemByReferenceIdSingleItem.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .referencedItemId(UUID.fromString(input.getReferencedItemId()))
                .price(10.00)
                .quantity(100)
                .build());

        getStorageItemByReferencedIdResult = GetStorageItemByReferencedIdResult.builder()
                .items(items)
                .build();


        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void throwsServiceUnavailableExceptionWhenStoreClientServiceIsUnavailable() {
        when(storeClient.getItemById(input.getReferencedItemId()))
                .thenThrow(new FeignException.FeignServerException(
                                -1,
                                "",
                                mock(Request.class),
                                new byte[0],
                                null
                        )
                );
        Assertions.assertThrows(ServiceUnavailableException.class, () -> processor.process(input));
    }

    @Test
    public void throwsStoreItemNotFoundExceptionWhenStoreClientServiceReturnsNotFound() {
        when(storeClient.getItemById(input.getReferencedItemId()))
                .thenThrow(new FeignException.FeignServerException(
                                404,
                                "",
                                mock(Request.class),
                                new byte[0],
                                null
                        )
                );
        Assertions.assertThrows(StoreItemNotFoundException.class, () -> processor.process(input));
    }

    @Test
    public void throwsServiceUnavailableExceptionWhenStorageClientServiceIsUnavailable() {
        when(storeClient.getItemById(input.getReferencedItemId())).thenReturn(GetItemByIdResult.builder().build());
        when(storageClient.getItemByReferencedItemId(Set.of(input.getReferencedItemId())))
                .thenThrow(new FeignException.FeignServerException(
                                -1,
                                "",
                                mock(Request.class),
                                new byte[0],
                                null
                        )
                );

        Assertions.assertThrows(ServiceUnavailableException.class, () -> processor.process(input));
    }

    @Test
    public void throwsStoreItemNotFoundExceptionWhenStorageClientServiceReturnsNotFound() {
        when(storeClient.getItemById(input.getReferencedItemId())).thenReturn(GetItemByIdResult.builder().build());
        when(storageClient.getItemByReferencedItemId(Set.of(input.getReferencedItemId())))
                .thenThrow(new FeignException.FeignServerException(
                                404,
                                "",
                                mock(Request.class),
                                new byte[0],
                                null
                        )
                );

        Assertions.assertThrows(StoreItemNotFoundException.class, () -> processor.process(input));
    }

    @Test
    public void throwsUsernameNotFoundExceptionWhenCurrentUserIsNull() {
        when(storeClient.getItemById(input.getReferencedItemId())).thenReturn(GetItemByIdResult.builder().build());
        when(storageClient.getItemByReferencedItemId(Set.of(input.getReferencedItemId())))
                .thenReturn(getStorageItemByReferencedIdResult);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("invalid@email");

        Assertions.assertThrows(UsernameNotFoundException.class, () -> processor.process(input));
    }

    @Test
    public void returnsCartItemWhenCartIsEmpty() {
        when(storeClient.getItemById(input.getReferencedItemId())).thenReturn(GetItemByIdResult.builder().build());
        when(storageClient.getItemByReferencedItemId(Set.of(input.getReferencedItemId())))
                .thenReturn(getStorageItemByReferencedIdResult);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("valid@email");
        when(userRepository.findByEmail("valid@email")).thenReturn(Optional.of(user));

        when(cartItemRepository.findCartItemByReferencedItemId(UUID.fromString(this.input.getReferencedItemId())))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        AddCartItemResult expected = new AddCartItemResult(Set.of("00000000-0000-0000-0000-000000000000|10|10.00"));
        Assertions.assertEquals(expected.getCartContents().toString(), processor.process(input).getCartContents().toString());
    }

    @Test
    public void addsCartItemWhenItemExistsInCart() {
        when(storeClient.getItemById(input.getReferencedItemId())).thenReturn(GetItemByIdResult.builder().build());
        when(storageClient.getItemByReferencedItemId(Set.of(input.getReferencedItemId())))
                .thenReturn(getStorageItemByReferencedIdResult);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("valid@email");
        when(userRepository.findByEmail("valid@email")).thenReturn(Optional.of(user));

        when(cartItemRepository.findCartItemByReferencedItemId(UUID.fromString(this.input.getReferencedItemId())))
                .thenReturn(Optional.of(existingCartItem));

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        AddCartItemResult expected = new AddCartItemResult(Set.of("00000000-0000-0000-0000-000000000000|1010|10.00"));
        Assertions.assertEquals(expected.getCartContents().toString(), processor.process(input).getCartContents().toString());
    }
}