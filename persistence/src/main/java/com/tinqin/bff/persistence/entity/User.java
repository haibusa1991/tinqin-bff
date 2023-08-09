package com.tinqin.bff.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

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

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "user")
    private Set<CartItem> cartItems;

    public boolean addCartItem(CartItem cartItem){
        return this.cartItems.add(cartItem);
    }

    public boolean removeCartItem(CartItem cartItem){
        return this.cartItems.remove(cartItem);
    }

}
