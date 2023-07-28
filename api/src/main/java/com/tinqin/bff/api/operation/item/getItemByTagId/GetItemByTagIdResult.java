package com.tinqin.bff.api.operation.item.getItemByTagId;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class GetItemByTagIdResult implements ProcessorResult {
    private List<GetItemByTagIdSingleItem> items;
}
