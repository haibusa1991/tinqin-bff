package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperation;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperationInput;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherResult;
import com.tinqin.bff.persistence.entity.CartItem;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.repository.CartItemRepository;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResult;
import com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderResultSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
import com.tinqin.storage.restexport.StorageRestExport;
import com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations= "classpath:application.yaml")
@ExtendWith(SpringExtension.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @MockBean
    private ZooStoreRestExport zoostoreClient;

    @MockBean
    private StorageRestExport storageClient;

    @MockBean
    private PurchaseVoucherOperation purchaseVoucher;

    private String authorization;

    private final String loginInfo = """
            {
                "email":"test@test",
                "password":"password"
            }
            """;

    private final String registerInfo = """
            {
                "email":"test@test",
                "password":"password",
                "firstName":"test first name",
                "lastName":"test last name",
                "phoneNumber":"test phone number"
            }
            """;


    @BeforeAll()
    void registerAndLoginUser() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerInfo))
                .andReturn();

        this.authorization = "Bearer " + mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginInfo))
                .andReturn()
                .getResponse()
                .getHeader("Authorization");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        this.cartItemRepository.deleteAll();
    }

    @Test
    void addCartItemReturn200WhenItemIsAddedAndCartIsEmpty() throws Exception {
        when(zoostoreClient.getItemById(any(String.class))).thenReturn(mock(GetItemByIdResult.class));

        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        GetStorageItemByReferenceIdSingleItem singleItem = GetStorageItemByReferenceIdSingleItem
                .builder()
                .referencedItemId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .quantity(100)
                .price(10.0)
                .build();

        GetStorageItemByReferencedIdResult storage = GetStorageItemByReferencedIdResult.builder().items(List.of(singleItem)).build();

        when(this.storageClient.getItemByReferencedItemId(any(Set.class))).thenReturn(storage);

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents.size()").value(1))
                .andExpect(jsonPath("$.cartContents[0]").value("00000000-0000-0000-0000-000000000000|1|10.00"))
                .andExpect(e -> Assertions.assertEquals(1, this.cartItemRepository.count()))
                .andExpect(e -> Assertions.assertEquals("00000000-0000-0000-0000-000000000000", this.cartItemRepository
                        .findAll()
                        .get(0)
                        .getReferencedItemId().toString()))
                .andExpect(e -> Assertions.assertEquals(1, this.userRepository
                        .findAll()
                        .get(0)
                        .getCartItems().size()))
                .andExpect(e -> Assertions.assertEquals("00000000-0000-0000-0000-000000000000", this.userRepository
                        .findAll()
                        .get(0)
                        .getCartItems()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getReferencedItemId()
                        .toString()))
                .andReturn();
    }

    @Test
    void addCartItemReturn200WhenItemIsAddedAndCartHasAnotherItem() throws Exception {

        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();
        CartItem cartItem = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted = this.cartItemRepository.save(cartItem);
        user.addCartItem(persisted);
        this.userRepository.save(user);


        when(zoostoreClient.getItemById(any(String.class))).thenReturn(mock(GetItemByIdResult.class));
        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        GetStorageItemByReferenceIdSingleItem singleItem = GetStorageItemByReferenceIdSingleItem
                .builder()
                .referencedItemId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .quantity(100)
                .price(10.00)
                .build();

        GetStorageItemByReferencedIdResult storage = GetStorageItemByReferencedIdResult.builder().items(List.of(singleItem)).build();

        when(this.storageClient.getItemByReferencedItemId(any(Set.class))).thenReturn(storage);

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents.size()").value(2))
                .andExpect(jsonPath("$.cartContents[0]").value("00000000-0000-0000-0000-000000000000|1|10.00"))
                .andExpect(jsonPath("$.cartContents[1]").value("99999999-9999-9999-9999-999999999999|1|10.00"))
                .andReturn();
    }

    @Test
    void addCartItemReturn200AndSumsCorrectlyWhenItemIsAddedAndCartAlreadyHasTheSameItem() throws Exception {

        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();
        CartItem cartItem = CartItem.builder()
                .referencedItemId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted = this.cartItemRepository.save(cartItem);
        user.addCartItem(persisted);
        this.userRepository.save(user);

        when(zoostoreClient.getItemById(any(String.class))).thenReturn(mock(GetItemByIdResult.class));
        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        GetStorageItemByReferenceIdSingleItem singleItem = GetStorageItemByReferenceIdSingleItem
                .builder()
                .referencedItemId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .quantity(100)
                .price(10.00)
                .build();

        GetStorageItemByReferencedIdResult storage = GetStorageItemByReferencedIdResult.builder().items(List.of(singleItem)).build();

        when(this.storageClient.getItemByReferencedItemId(any(Set.class))).thenReturn(storage);

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents.size()").value(1))
                .andExpect(jsonPath("$.cartContents[0]").value("00000000-0000-0000-0000-000000000000|2|10.00"))
                .andReturn();
    }

    @Test
    void addCartItemReturn400WhenReferencedItemIdIsNotValidUuid() throws Exception {
        String content = """
                {
                  "referencedItemId": "invalidUuid",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("referencedItemId: 'must be a valid UUID'"));
    }

    @Test
    void addCartItemReturn400WhenQuantityIsZero() throws Exception {
        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 0
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("quantity: 'must be greater than 0'"));
    }

    @Test
    void addCartItemReturn400WhenQuantityIsNegative() throws Exception {
        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": -1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("quantity: 'must be greater than 0'"));
    }

    @Test
    void addCartItemReturn403WhenAuthorizationHeaderNotPresent() throws Exception {
        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$").value("Invalid token."));
    }

    @Test
    void addCartItemReturn404WhenReferencedItemIdIsNonExistentInZoostore() throws Exception {
        when(zoostoreClient.getItemById(any(String.class))).thenThrow(new FeignException.FeignServerException(
                404,
                "",
                mock(Request.class),
                new byte[0],
                null
        ));

        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("No item with id '00000000-0000-0000-0000-000000000000' found in store."));
    }

    @Test
    void addCartItemReturn503WhenZoostoreServiceIsNotAvailable() throws Exception {
        when(zoostoreClient.getItemById(any(String.class))).thenThrow(new FeignException.FeignServerException(
                -1,
                "",
                mock(Request.class),
                new byte[0],
                null
        ));

        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(503))
                .andExpect(jsonPath("$").value("Service 'zoostore' is not available."));
    }

    @Test
    void addCartItemReturn404WhenReferencedItemIdIsNonExistentInStorage() throws Exception {
        when(zoostoreClient.getItemById(any(String.class))).thenReturn(mock(GetItemByIdResult.class));
        when(this.storageClient.getItemByReferencedItemId(any(Set.class))).thenThrow(new FeignException.FeignServerException(
                404,
                "",
                mock(Request.class),
                new byte[0],
                null
        ));

        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("No item with id '00000000-0000-0000-0000-000000000000' found in store."));
    }

    @Test
    void addCartItemReturn503WhenStorageServiceIsNotAvailable() throws Exception {
        when(zoostoreClient.getItemById(any(String.class))).thenReturn(mock(GetItemByIdResult.class));
        when(this.storageClient.getItemByReferencedItemId(any(Set.class))).thenThrow(new FeignException.FeignServerException(
                -1,
                "",
                mock(Request.class),
                new byte[0],
                null
        ));

        String content = """
                {
                  "referencedItemId": "00000000-0000-0000-0000-000000000000",
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is(503))
                .andExpect(jsonPath("$").value("Service 'storage' is not available."));
    }

    @Test
    void getCartContentsReturns403WhenAuthorizationHeaderNotPresent() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$").value("Invalid token."));
    }

    @Test
    void getCartContentsReturns200WhenNoItemsAreAdded() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();
        mockMvc.perform(get("/cart")
                        .header("Authorization", this.authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents").isEmpty())
                .andExpect(e -> Assertions.assertTrue(this.cartItemRepository.findAllByUser(user).isEmpty()));
    }

    @Test
    void getCartContentsReturnsCorrectWithExistingItems() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();

        CartItem cartItem1 = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .referencedItemId(UUID.fromString("88888888-8888-8888-8888-888888888888"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted1 = this.cartItemRepository.save(cartItem1);
        CartItem persisted2 = this.cartItemRepository.save(cartItem2);
        user.addCartItem(persisted1);
        user.addCartItem(persisted2);

        this.userRepository.save(user);

        mockMvc.perform(get("/cart")
                        .header("Authorization", this.authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents.size()").value(2))
                .andExpect(jsonPath("$.cartContents[0]").value("99999999-9999-9999-9999-999999999999|1|10.00"))
                .andExpect(jsonPath("$.cartContents[1]").value("88888888-8888-8888-8888-888888888888|1|10.00"));
    }

    @Test
    void emptyCartContentsReturns204AndDeletesCartContents() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();

        CartItem cartItem1 = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .referencedItemId(UUID.fromString("88888888-8888-8888-8888-888888888888"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted1 = this.cartItemRepository.save(cartItem1);
        CartItem persisted2 = this.cartItemRepository.save(cartItem2);
        user.addCartItem(persisted1);
        user.addCartItem(persisted2);

        this.userRepository.save(user);

        mockMvc.perform(delete("/cart")
                        .header("Authorization", this.authorization))
                .andExpect(status().isNoContent())
                .andExpect(e -> Assertions.assertEquals(0, this.cartItemRepository.count()))
                .andExpect(e -> Assertions.assertEquals(0, this.userRepository.findAll().get(0).getCartItems().size()));
    }

    @Test
    void emptyCartContentsReturns403WhenAuthorizationHeaderNotPresent() throws Exception {
        mockMvc.perform(delete("/cart"))
                .andExpect(status().is(403));
    }

    @Test
    void removeCartItemReturns200WhenRemovingItem() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();

        CartItem cartItem1 = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .referencedItemId(UUID.fromString("88888888-8888-8888-8888-888888888888"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted1 = this.cartItemRepository.save(cartItem1);
        CartItem persisted2 = this.cartItemRepository.save(cartItem2);
        user.addCartItem(persisted1);
        user.addCartItem(persisted2);

        this.userRepository.save(user);

        mockMvc.perform(delete("/cart/88888888-8888-8888-8888-888888888888")
                        .header("Authorization", this.authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartContents.size()").value(1))
                .andExpect(jsonPath("$.cartContents[0]").value("99999999-9999-9999-9999-999999999999|1|10.00"))
                .andExpect(e -> Assertions.assertEquals(1, this.cartItemRepository.count()))
                .andExpect(e -> Assertions.assertEquals("99999999-9999-9999-9999-999999999999", this.cartItemRepository
                        .findAll()
                        .get(0)
                        .getReferencedItemId()
                        .toString()))
                .andExpect(e -> Assertions.assertEquals(1, this.userRepository.findAll().get(0).getCartItems().size()))
                .andExpect(e -> Assertions.assertEquals("99999999-9999-9999-9999-999999999999", this.userRepository
                        .findAll()
                        .get(0)
                        .getCartItems()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getReferencedItemId()
                        .toString()));
    }

    @Test
    void removeCartItemReturns403WhenAuthorizationHeaderNotPresent() throws Exception {
        mockMvc.perform(delete("/cart/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().is(403));
    }

    @Test
    void removeCartItemReturns404WhenRemovingNonexistentItem() throws Exception {
        mockMvc.perform(delete("/cart/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", this.authorization))
                .andExpect(status().is(404));
    }

    @Test
    void removeCartItemReturns409WhenReferencedItemIdIsInvalidUuid() throws Exception {
        mockMvc.perform(delete("/cart/invalid")
                        .header("Authorization", this.authorization))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$").value("must be a valid UUID"));
    }


    @Test
    void placeOrderReturns200OnSuccessfulOrder() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();

        CartItem cartItem = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted = this.cartItemRepository.save(cartItem);
        user.addCartItem(persisted);
        this.userRepository.save(user);

        PlaceOrderResultSingleItem item = PlaceOrderResultSingleItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(10.00)
                .quantity(1)
                .build();

        LocalDateTime timestamp = LocalDateTime.now();
        PlaceOrderResult placeOrder = PlaceOrderResult.builder()
                .items(List.of(item))
                .timestamp(timestamp)
                .user(user.getId())
                .remainingUserCredit(0.00)
                .orderPrice(10.00)
                .build();

        when(storageClient.placeOrder(any(com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput.class)))
                .thenReturn(placeOrder);

        when(purchaseVoucher.process(any(PurchaseVoucherOperationInput.class))).thenReturn(mock(PurchaseVoucherResult.class));

        mockMvc.perform(post("/cart/place-order")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].referencedItemId").value("99999999-9999-9999-9999-999999999999"))
                .andExpect(jsonPath("$.items[0].price").value(10.00))
                .andExpect(jsonPath("$.items[0].quantity").value(1))
                .andExpect(jsonPath("$.timestamp").value(timestamp.toString().subSequence(0, 27)))
                .andExpect(jsonPath("$.user").value(user.getId().toString()))
                .andExpect(jsonPath("$.orderPrice").value(10.00))
                .andExpect(e -> Assertions.assertEquals(0, this.cartItemRepository.count()))
                .andExpect(e -> Assertions.assertEquals(0, this.userRepository.findAll().get(0).getCartItems().size()));
    }

    @Test
    void placeOrderReturns403WhenAuthorizationHeaderNotPresent() throws Exception {
        mockMvc.perform(post("/cart/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(403));
    }

    @Test
    void placeOrderReturns404WhenOrderingEmptyCart() throws Exception {
        mockMvc.perform(post("/cart/place-order")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void placeOrderReturns503WhenStorageServiceIsUnavailable() throws Exception {
        User user = this.userRepository.findAll().stream().findFirst().orElseThrow();

        CartItem cartItem = CartItem.builder()
                .referencedItemId(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .price(BigDecimal.valueOf(10.00))
                .user(user)
                .quantity(1)
                .build();

        CartItem persisted = this.cartItemRepository.save(cartItem);
        user.addCartItem(persisted);
        this.userRepository.save(user);

        when(this.storageClient.placeOrder(any(com.tinqin.storage.api.operations.order.placeOrder.PlaceOrderInput.class)))
                .thenThrow(new FeignException.FeignServerException(
                        -1,
                        "",
                        mock(Request.class),
                        new byte[0],
                        null
                ));

        mockMvc.perform(post("/cart/place-order")
                        .header("Authorization", this.authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(503))
                .andExpect(jsonPath("$").value("Service 'storage' is not available."));
    }

}