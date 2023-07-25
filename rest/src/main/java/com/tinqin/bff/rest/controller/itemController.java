package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.item.GetItemByIdInput;
import com.tinqin.bff.api.operation.item.GetItemByIdOperation;
import com.tinqin.bff.api.operation.item.GetItemByIdResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class itemController {
    private final GetItemByIdOperation getItemById;

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<GetItemByIdResult> getItemById(@PathVariable @org.hibernate.validator.constraints.UUID String itemId) {
        return ResponseEntity.ok(this.getItemById.process(GetItemByIdInput.builder().referencedItemId(UUID.fromString(itemId)).build()));
    }
}
