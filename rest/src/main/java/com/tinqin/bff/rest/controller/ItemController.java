package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.createItem.CreateItemInput;
import com.tinqin.bff.api.operation.createItem.CreateItemOperation;
import com.tinqin.bff.api.operation.createItem.CreateItemResult;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitle;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleInput;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleResult;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdInput;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdOperation;
import com.tinqin.bff.api.operation.item.getItemByTagId.GetItemByTagIdResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final GetItemByTagIdOperation getItemByTagId;
    private final GetItemByPartialTitle getItemByPartialTitle;
    private final CreateItemOperation createItem;


    @Operation(description = "Gets all items having the specified tag and paginates them according the parameters specified.",
            summary = "Gets item by tag id.")
    @ApiResponse(responseCode = "200", description = "Items found.")
    @ApiResponse(responseCode = "400",
            description = "Tag id is not a valid UUID",
            content = {@Content(examples = @ExampleObject(value = "must be a valid UUID"), mediaType = "text/html")})
    @ApiResponse(responseCode = "403",
            description = "JWT is invalid.",
            content = {@Content(examples = @ExampleObject(value = ""), mediaType = "text/html")})
    @ApiResponse(responseCode = "404",
            description = "Specified tag does not exist.",
            content = {@Content(examples = @ExampleObject(value = "No tag with id '4714a487-ea4c-42d3-a5df-af4e11bc7bcb' exists."), mediaType = "text/html")})
    @ApiResponse(responseCode = "409",
            description = "itemsPerPage or page is less than 1",
            content = {@Content(examples = @ExampleObject(value = "must be greater than 0"), mediaType = "text/html")})
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

    @GetMapping(path = "/partial")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<GetItemByPartialTitleResult> getItemByPartialTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "2") @Positive Integer itemsPerPage,
            @RequestParam(defaultValue = "1") @Positive Integer page) {

        GetItemByPartialTitleInput input = GetItemByPartialTitleInput.builder()
                .title(title)
                .itemsPerPage(itemsPerPage)
                .page(page)
                .build();

        return new ResponseEntity<>(this.getItemByPartialTitle.process(input), HttpStatus.OK);
    }


    @Operation(description = "Creates new item with the specified parameters.",
            summary = "Creates new item.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter specified"),
            @ApiResponse(responseCode = "403",
                    description = "JWT is invalid.",
                    content = {@Content(examples = @ExampleObject(value = ""), mediaType = "text/html")}),
            @ApiResponse(responseCode = "404",
                    description = "Specified vendor/tag/multimedia does not exist.",
                    content = {@Content(examples = @ExampleObject(value = "No tag with id '4714a487-ea4c-42d3-a5df-af4e11bc7bcb' exists."), mediaType = "text/html")}),
            @ApiResponse(responseCode = "409",
                    description = "Price must be greater than 0",
                    content = {@Content(examples = @ExampleObject(value = "must be greater than 0"), mediaType = "text/html")})})
    @PostMapping()
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<CreateItemResult> createNewItem(CreateItemInput input) {
        return new ResponseEntity<>(this.createItem.process(input), HttpStatus.CREATED);
    }
}
