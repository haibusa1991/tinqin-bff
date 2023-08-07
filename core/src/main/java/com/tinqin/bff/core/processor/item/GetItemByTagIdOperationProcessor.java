package com.tinqin.bff.core.processor.item;

import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdInput;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdOperation;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdResult;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdSingleItem;
import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.TagNotFoundException;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
import com.tinqin.storage.restexport.StorageItemRestExport;
import com.tinqin.zoostore.api.operations.item.BaseEditItemResult;
import com.tinqin.zoostore.api.operations.item.getAllItem.GetAllItemOperationProcessorSingleItem;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetItemByTagIdOperationProcessor implements GetItemByTagIdOperation {
    private final StorageItemRestExport storageClient;
    private final ZooStoreRestExport zooStoreClient;

    @Override
    public GetItemByTagIdResult process(GetItemByTagIdInput input) {

        Set<GetAllItemOperationProcessorSingleItem> items;
        try {
            items = zooStoreClient.getAllItems(input.getIncludeArchived(),
                    input.getTagId().toString(),
                    input.getItemsPerPage(),
                    input.getPage()
            ).getItems();
        }catch (FeignException e){
            switch (e.status()) {
                case (-1) -> throw new ServiceUnavailableException("storage");
                case (404) -> throw new TagNotFoundException(input.getTagId());
                default -> throw new RuntimeException(e);
            }
        }

//        List<GetStorageItemByReferenceIdSingleItem> storages = this.storageClient.getItemByReferencedItemId(items
//                .stream()
//                .map(BaseEditItemResult::getId)
//                .map(UUID::toString)
//                .collect(Collectors.toSet())
//        ).getItems();

        List<GetStorageItemByReferenceIdSingleItem> storages = items.parallelStream()
                .map(BaseEditItemResult::getId)
                .map(UUID::toString)
                .map(this::getStorageItem)
                .map(GetStorageItemByReferencedIdResult::getItems)
                .flatMap(Collection::stream)
                .toList();


        return GetItemByTagIdResult.builder()
                .items(items.stream()
                        .map(e -> this.matchItemToStorage(e, storages))
                        .map(this::mapToSingleItem)
                        .toList()
                ).build();
    }

    private Map.Entry<GetAllItemOperationProcessorSingleItem, GetStorageItemByReferenceIdSingleItem> matchItemToStorage(GetAllItemOperationProcessorSingleItem item, List<GetStorageItemByReferenceIdSingleItem> storages) {

        return Map.entry(item, storages.stream()
                .filter(e -> e.getReferencedItemId().equals(item.getId()))
                .findFirst()
                .orElseThrow());
    }

    private GetItemByTagIdSingleItem mapToSingleItem(Map.Entry<GetAllItemOperationProcessorSingleItem, GetStorageItemByReferenceIdSingleItem> pair) {

        return GetItemByTagIdSingleItem.builder()
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


    private GetStorageItemByReferencedIdResult getStorageItem(String tagId) {
        try {
            return this.storageClient.getItemByReferencedItemId(Set.of(tagId));
        } catch (FeignException e) {
            switch (e.status()) {
                case (-1) -> throw new ServiceUnavailableException("storage");
                case (404) -> throw new TagNotFoundException(UUID.fromString(tagId));
                default -> throw new RuntimeException(e);
            }
        }
    }
}
