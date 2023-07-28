//package com.tinqin.bff.core.processor.item;
//
//import com.tinqin.bff.api.operation.item.getItemById.GetItemByIdInput;
//import com.tinqin.bff.api.operation.item.getItemById.GetItemByIdOperation;
//import com.tinqin.bff.api.operation.item.getItemById.GetItemByIdResult;
//import com.tinqin.bff.core.exception.ServiceUnavailableException;
//import com.tinqin.bff.core.exception.StorageItemNotFoundException;
//import com.tinqin.bff.core.exception.StoreItemNotFoundException;
//import com.tinqin.storage.api.operations.storageItem.getStorageItemByReferencedId.GetStorageItemByReferencedIdResult;
//import com.tinqin.storage.restexport.StorageItemRestExport;
//import com.tinqin.zoostore.restexport.ZooStoreRestExport;
//import feign.FeignException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class GetItemByIdOperationProcessor implements GetItemByIdOperation {
//    private final StorageItemRestExport storageClient;
//    private final ZooStoreRestExport zooStoreClient;
//
//
//    @Override
//    public GetItemByIdResult process(GetItemByIdInput input) {
//
//        com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult storeItem=new com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult();
//        try {
//            storeItem = zooStoreClient.getItemById(input.getReferencedItemId().toString());
//        } catch (FeignException e) {
//            switch (e.status()) {
//                case (-1) -> throw new ServiceUnavailableException("ZooStore");
//                case (404) -> throw new StoreItemNotFoundException(input.getReferencedItemId());
//            }
//        }
//
//        GetStorageItemByReferencedIdResult storageItem = new GetStorageItemByReferencedIdResult();
//        try {
//           storageItem = storageClient.getItemByReferencedItemId(input.getReferencedItemId().toString());
//        } catch (FeignException e) {
//            switch (e.status()) {
//                case (-1) -> throw new ServiceUnavailableException("Storage");
//                case (404) -> throw new StorageItemNotFoundException(input.getReferencedItemId());
//            }
//        }
//
//        return GetItemByIdResult.builder()
//                .storeItemId(storeItem.getId())
//                .storeItemTitle(storeItem.getTitle())
//                .storeItemDescription(storeItem.getDescription())
//                .storeItemVendorId(storeItem.getVendorId())
//                .storeItemMultimedia(storeItem.getMultimedia())
//                .storeItemTags(storeItem.getTags())
//
//                .storageItemItemId(storageItem.getId())
//                .storageItemPrice(storageItem.getPrice())
//                .storageItemQuantity(storageItem.getQuantity())
//                .build();
//    }
//}
