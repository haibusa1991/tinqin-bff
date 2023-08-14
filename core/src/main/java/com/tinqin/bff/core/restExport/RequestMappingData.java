package com.tinqin.bff.core.restExport;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestMappingData {

    private MethodAnnotation methodAnnotation;
    private String pathValue;
}
