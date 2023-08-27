package com.tinqin.bff.api.operation.createItem;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class CreateItemResult implements ProcessorResult {
    private UUID id;
    private String title;
    private String description;
    private UUID vendorId;
    private UUID[] multimedia;
    private UUID[] tags;
    private Double price;
    private Integer quantity;
}
