package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdInput;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdOperation;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class itemController {
    private final GetItemByTagIdOperation getItemByTagId;
//    private final GetItemByIdOperation getItemById;

//    @GetMapping(path = "/{itemId}")
//    public ResponseEntity<GetItemByIdResult> getItemById(@PathVariable @org.hibernate.validator.constraints.UUID String itemId) {
//        return ResponseEntity.ok(this.getItemById.process(GetItemByIdInput.builder().referencedItemId(UUID.fromString(itemId)).build()));
//    }

    @Operation(summary = "DB is corrupted, only 4714a487-ea4c-42d3-a5df-af4e11bc7bca works")
    @GetMapping
    public ResponseEntity<GetItemByTagIdResult> getAllItems(
            @RequestParam(required = false, defaultValue = "false") Boolean includeArchived,
            @RequestParam @UUID String tagId,
            @RequestParam(defaultValue = "2") Integer itemsPerPage,
            @RequestParam(defaultValue = "1") Integer page
    ) {

        GetItemByTagIdInput input = GetItemByTagIdInput.builder()
                .includeArchived(includeArchived)
                .tagId(java.util.UUID.fromString(tagId))
                .itemsPerPage(itemsPerPage)
                .page(page)
                .build();

        return ResponseEntity.ok(this.getItemByTagId.process(input));
    }
}
