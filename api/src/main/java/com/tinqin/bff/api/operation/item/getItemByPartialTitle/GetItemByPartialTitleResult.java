package com.tinqin.bff.api.operation.item.getItemByPartialTitle;

import com.tinqin.bff.api.base.ProcessorResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.PRIVATE)
@Getter
@Builder
public class GetItemByPartialTitleResult implements ProcessorResult {
    private List<GetItemByPartialTitleSingleItem> items;
}
