package com.tinqin.bff.api.operation.item.getItemByTagId;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class GetItemByTagIdInput implements ProcessorInput {
    private UUID tagId;
    private Boolean includeArchived;
    private Integer page;
    private Integer itemsPerPage;
}