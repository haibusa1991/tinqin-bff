package com.tinqin.bff.core.restExport;

import java.util.zip.ZipEntry;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers({
    "Content-Type: application/json"
})
public interface GeneratedRestExport {

    @RequestLine("PUT /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String putWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2,
        @Param ZipEntry myPojoTest);

    @RequestLine("POST /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String postWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2,
        @Param ZipEntry myPojoTest);

    @RequestLine("PATCH /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String patchWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2,
        @Param ZipEntry myPojoTest);

    @RequestLine("GET /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String getWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2);

    @RequestLine("DELETE /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String deleteWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2,
        @Param ZipEntry myPojoTest);
}
