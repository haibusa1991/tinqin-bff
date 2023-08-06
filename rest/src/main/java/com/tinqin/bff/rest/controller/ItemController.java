package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdInput;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdOperation;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final GetItemByTagIdOperation getItemByTagId;



    @Operation(description = "Gets all items having the specified tag and paginates them according the parameters specified.",
            summary = "Gets item by tag id.")
    @ApiResponse(responseCode = "200", description = "Items found.")
    @ApiResponse(responseCode = "400", description = "Tag id is invalid, itemsPerPage or page is less than 1")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<GetItemByTagIdResult> getAllItems(
            @RequestParam(required = false, defaultValue = "false") Boolean includeArchived,
            @RequestParam @UUID String tagId,
            @RequestParam(defaultValue = "2") @Positive Integer itemsPerPage,
            @RequestParam(defaultValue = "1") @Positive Integer page
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
