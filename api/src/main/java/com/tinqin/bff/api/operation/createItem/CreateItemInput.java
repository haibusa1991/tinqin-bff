package com.tinqin.bff.api.operation.createItem;


import com.tinqin.bff.api.base.ProcessorInput;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class CreateItemInput implements ProcessorInput {
    private String title;
    private String description;
    @org.hibernate.validator.constraints.UUID
    private UUID vendorId;
    @org.hibernate.validator.constraints.UUID
    private Set<UUID> multimedia;
    @org.hibernate.validator.constraints.UUID
    private Set<UUID> tags;

    @Positive
    private Double price;
    @Positive
    private Integer quantity;


}