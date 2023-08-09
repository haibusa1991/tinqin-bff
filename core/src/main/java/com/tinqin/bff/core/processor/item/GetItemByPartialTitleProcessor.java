package com.tinqin.bff.core.processor.item;

import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitle;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleInput;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleResult;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StorageItemNotFoundException;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
import com.tinqin.storage.restexport.StorageItemRestExport;
import com.tinqin.zoostore.api.operations.item.BaseEditItemResult;
import com.tinqin.zoostore.api.operations.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetItemByPartialTitleProcessor implements GetItemByPartialTitle {
    private final ZooStoreRestExport zoostoreClient;
    private final StorageItemRestExport storageClient;

    @Override
    public GetItemByPartialTitleResult process(GetItemByPartialTitleInput input) {

        Set<GetItemByPartialTitleSingleItem> items;
        try {
            items = this.zoostoreClient.getItemByPartialTitle(input.getTitle(), input.getItemsPerPage(), input.getPage()).getItems();
        } catch (FeignException e) {
            switch (e.status()) {
                case (-1) -> throw new ServiceUnavailableException("storage");
                default -> throw new RuntimeException(e); //TODO - fix duplicate
            }
        }

        List<GetStorageItemByReferenceIdSingleItem> storages = items.parallelStream()
                .map(BaseEditItemResult::getId)
                .map(UUID::toString)
                .map(this::getStorageItem)
                .map(GetStorageItemByReferencedIdResult::getItems)
                .flatMap(Collection::stream)
                .toList();


        return GetItemByPartialTitleResult.builder()
                .items(items.stream()
                        .map(e -> this.matchItemToStorage(e, storages))
                        .map(this::mapToSingleItem)
                        .toList()
                ).build();
    }

    private Map.Entry<com.tinqin.zoostore.api.operations.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem, GetStorageItemByReferenceIdSingleItem>
    matchItemToStorage(com.tinqin.zoostore.api.operations.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem item, List<GetStorageItemByReferenceIdSingleItem> storages) {

        return Map.entry(item, storages.stream()
                .filter(e -> e.getReferencedItemId().equals(item.getId()))
                .findFirst()
                .orElseThrow());
    }

    private com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem mapToSingleItem(Map.Entry<com.tinqin.zoostore.api.operations.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem, GetStorageItemByReferenceIdSingleItem> pair) {

        return com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleSingleItem
                .builder()
                .storeItemId(pair.getKey().getId())
                .storeItemTitle(pair.getKey().getTitle())
                .storeItemDescription(pair.getKey().getDescription())
                .storeItemVendorId(pair.getKey().getVendorId())
                .storeItemMultimedia(pair.getKey().getMultimedia())
                .storeItemTags(pair.getKey().getTags())

                .storageItemItemId(pair.getValue().getId())
                .storageItemPrice(pair.getValue().getPrice())
                .storageItemQuantity(pair.getValue().getQuantity())
                .build();
    }


    private GetStorageItemByReferencedIdResult getStorageItem(String referencedItemId) {
        try {
            return this.storageClient.getItemByReferencedItemId(Set.of(referencedItemId));
        } catch (FeignException e) {
            switch (e.status()) {
                case (-1) -> throw new ServiceUnavailableException("storage");
                default -> throw new StorageItemNotFoundException(UUID.fromString(referencedItemId));
            }
        }
    }
}
