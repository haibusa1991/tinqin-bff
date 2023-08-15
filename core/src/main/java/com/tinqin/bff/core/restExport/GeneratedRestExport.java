package com.tinqin.bff.core.restExport;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers({
    "Content-Type: application/json"
})
public interface GeneratedRestExport {

    @RequestLine("feignRequestGoesHere")
    String myMethod(@Param String param1);
}
