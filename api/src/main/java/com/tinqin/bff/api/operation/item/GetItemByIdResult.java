package com.tinqin.bff.api.operation.item;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class GetItemByIdResult implements ProcessorResult {
    private UUID storeItemId;
    private String storeItemTitle;
    private String storeItemDescription;
    private UUID storeItemVendorId;
    private UUID[] storeItemMultimedia;
    private UUID[] storeItemTags;

    private UUID storageItemItemId;
    private double storageItemPrice;
    private int storageItemQuantity;
}
