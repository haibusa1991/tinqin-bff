package com.tinqin.bff.core.processor.item;

import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitle;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleInput;
import com.tinqin.bff.api.operation.item.getItemByPartialTitle.GetItemByPartialTitleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetItemByPartialTitleProcessor implements GetItemByPartialTitle {
    @Override
    public GetItemByPartialTitleResult process(GetItemByPartialTitleInput input) {
        return null;
    }
}
