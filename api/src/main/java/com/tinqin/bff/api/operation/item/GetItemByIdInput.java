package com.tinqin.bff.api.operation.item;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder

public class GetItemByIdInput implements ProcessorInput {
    @JsonIgnore
    private UUID referencedItemId;
}