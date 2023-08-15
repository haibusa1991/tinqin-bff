package com.tinqin.bff.core.restExport;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

@Headers({
    "Content-Type: application/json"
})
public interface GeneratedRestExport {

    @RequestLine("GET /test")
    String simpleGet();

    @RequestLine("GET /test")
    ResponseEntity responseEntityGet();

    @RequestLine("GET /test/subpath")
    String getWithSubPath();

    @RequestLine("GET /test/{pathVariable}?param1={param1}")
    String getWithPathVariableAndOneQueryParam(@Param("pathVariable") String pathVariable, @Param("param1") String param1);

    @RequestLine("GET /test/{pathVariable}?customName={param1}&param2={param2}")
    String getWithPathVariableAndMultipleQueryParam(@Param("pathVariable") String pathVariable, @Param("param1") String customName, @Param("param2") String param2);

    @RequestLine("GET /test/{pathVariable}")
    String getWithPathVariable(@Param("pathVariable") String pathVariable);

    @RequestLine("GET /test?param1={param1}")
    String getWithOneQueryParam(@Param("param1") String param1);

    @RequestLine("GET /test?param1={param1}&param2={param2}")
    String getWithMultipleQueryParam(@Param("param1") String param1, @Param("param2") String param2);

    @RequestLine("GET /test/{customNamePathVariable}{pathVariable2}?customQueryParameterName={param1}&param2={param2}")
    String getWithMultiplePathVariablesAndMultipleQueryParam(@Param("customNamePathVariable") String customNamePathVariable,
        @Param("pathVariable2") String pathVariable2,
        @Param("param1") String customQueryParameterName,
        @Param("param2") String param2);
}
