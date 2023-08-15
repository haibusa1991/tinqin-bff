package com.tinqin.bff.core.restExport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class BiTuple<T1, T2> {

    private T1 firstElement;
    private T2 secondElement;
}
