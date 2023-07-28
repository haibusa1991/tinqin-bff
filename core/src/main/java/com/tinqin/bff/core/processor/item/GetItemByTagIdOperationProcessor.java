package com.tinqin.bff.core.processor.item;

import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdInput;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdOperation;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdResult;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdSingleItem;
import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferenceIdSingleItem;
import com.tinqin.storage.restexport.StorageItemRestExport;
import com.tinqin.zoostore.api.operations.item.BaseEditItemResult;
import com.tinqin.zoostore.api.operations.item.getAllItem.GetAllItemOperationProcessorSingleItem;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetItemByTagIdOperationProcessor implements GetItemByTagIdOperation {
    private final StorageItemRestExport storageClient;
    private final ZooStoreRestExport zooStoreClient;

    @Override
    public GetItemByTagIdResult process(GetItemByTagIdInput input) {

        Set<GetAllItemOperationProcessorSingleItem> items = zooStoreClient.getAllItems(input.getIncludeArchived(),
                input.getTagId().toString(),
                input.getItemsPerPage(),
                input.getPage()
        ).getItems();


        List<GetStorageItemByReferenceIdSingleItem> storages = this.storageClient.getItemByReferencedItemId(items
                .stream()
                .map(BaseEditItemResult::getId)
                .map(UUID::toString)
                .collect(Collectors.toSet())
        ).getItems();

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
}
