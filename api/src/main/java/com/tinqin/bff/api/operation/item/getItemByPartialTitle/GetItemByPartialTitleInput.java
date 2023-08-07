package com.tinqin.bff.api.operation.item.getItemByPartialTitle;


import com.tinqin.bff.api.base.ProcessorInput;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class GetItemByPartialTitleInput implements ProcessorInput {
    private String title;
    private Integer page;
    private Integer itemsPerPage;
}