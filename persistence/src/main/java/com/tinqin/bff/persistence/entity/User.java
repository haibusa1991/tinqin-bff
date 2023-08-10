package com.tinqin.bff.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Builder
    public User(String email, String password, String firstName, String lastName, String phoneNumber, UUID id, Set<CartItem> cartItems) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.credit = BigDecimal.ZERO;
        this.id = id;
        this.cartItems = cartItems;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private BigDecimal credit;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<CartItem> cartItems;

    public boolean addCartItem(CartItem cartItem) {
        return this.cartItems.add(cartItem);
    }

    public boolean removeCartItem(CartItem cartItem) {
        return this.cartItems.remove(cartItem);
    }

}
