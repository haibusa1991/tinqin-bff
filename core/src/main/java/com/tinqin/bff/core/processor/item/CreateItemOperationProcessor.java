package com.tinqin.bff.core.processor.item;

import com.tinqin.bff.api.operation.createItem.CreateItemInput;
import com.tinqin.bff.api.operation.createItem.CreateItemOperation;
import com.tinqin.bff.api.operation.createItem.CreateItemResult;
import com.tinqin.storage.api.operations.storageItem.createStorageItem.CreateStorageItemInput;
import com.tinqin.storage.api.operations.storageItem.createStorageItem.CreateStorageItemResult;
import com.tinqin.storage.api.operations.storageItem.editStorageItem.EditStorageItemInput;
import com.tinqin.storage.api.operations.storageItem.editStorageItem.EditStorageItemResult;
import com.tinqin.storage.restexport.StorageRestExport;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CreateItemOperationProcessor implements CreateItemOperation {
    private final StorageRestExport storageClient;
    private final ZooStoreRestExport storeClient;

    @Override
    public CreateItemResult process(CreateItemInput input) {
        System.out.println();
        com.tinqin.zoostore.api.operations.item.createItem.CreateItemInput createItemInput =
                com.tinqin.zoostore.api.operations.item.createItem.CreateItemInput
                        .builder()
                        .title(input.getTitle())
                        .description(input.getDescription())
                        .vendorId(input.getVendorId())
                        .multimedia(input.getMultimedia() == null ? new HashSet<>() : input.getMultimedia())
                        .tags(input.getTags() == null ? new HashSet<>() : input.getTags())
                        .build();

        com.tinqin.zoostore.api.operations.item.createItem.CreateItemResult storeItem = storeClient.createItem(createItemInput);

        CreateStorageItemInput storageItemInput = CreateStorageItemInput.builder()
                .referencedItemId(storeItem.getId().toString())
                .price(input.getPrice())
                .build();

        this.storageClient.createStorageItem(storageItemInput);

        EditStorageItemInput storageItemQuantity = EditStorageItemInput.builder()
                .referencedItemId(storeItem.getId())
                .quantityDifference(input.getQuantity())
                .build();

        EditStorageItemResult editStorageItemResult = this.storageClient.editStorageItem(storeItem.getId().toString(), storageItemQuantity);


        CreateItemResult build = CreateItemResult.builder()
                .id(storeItem.getId())
                .title(storeItem.getTitle())
                .description(storeItem.getDescription())
                .vendorId(storeItem.getVendorId())
                .multimedia(storeItem.getMultimedia())
                .tags(storeItem.getTags())
                .price(editStorageItemResult.getPrice())
                .quantity(editStorageItemResult.getQuantity())
                .build();
        return build;
    }
}
