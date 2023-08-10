package com.tinqin.bff.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vouchers")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class Voucher {

    @Builder
    public Voucher(String code, Integer value, LocalDateTime validUntil) {
        this.code = code;
        this.value = value;
        this.validUntil = validUntil;
        this.isExhausted = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code;

    private Integer value;
    private LocalDateTime validUntil;

    @Setter
    private Boolean isExhausted;
}
