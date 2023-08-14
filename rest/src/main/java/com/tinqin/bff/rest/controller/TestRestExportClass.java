package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.annotations.RestExport;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
public class TestRestExportClass {

    @GetMapping
    private String unannotatedMethod() {
        return "unannotated get";
    }

//    @RestExport
//    @RequestMapping(method = RequestMethod.POST)
//    private String requestMappingGet() {
//        return "request mapping get";
//    }

    @RestExport
    @GetMapping
    private String simpleGet() {
        return "simple get";
    }

    @RestExport
    @GetMapping(path = "/subpath")
    private String getWithSubPath() {
        return "getWithSubPath";
    }

    @RestExport
    @GetMapping(path = "/{pathVariable}")
    private String getWithPathVariable(@PathVariable String pathVariable) {
        return "getWithPathVariable - " + pathVariable;
    }


    @RestExport
    @GetMapping
    private String getWithOneQueryParam(@RequestParam String param1) {
        return "getWithOneQueryParam - " + param1;
    }

    @RestExport
    @GetMapping
    private String getWithMultipleQueryParam(@RequestParam String param1, @RequestParam String param2) {
        return String.format("getWithMultipleQueryParam - %s; %s", param1, param2);
    }

    @RestExport
    @GetMapping(path = "/{pathVariable}")
    private String getWithPathVariableAndOneQueryParam(@PathVariable String pathVariable, @RequestParam String param1) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable: %s; param - %s", pathVariable, param1);
    }

    @RestExport
    @GetMapping(path = "/{pathVariable}")
    private String getWithPathVariableAndMultipleQueryParam(@PathVariable String pathVariable, @RequestParam String param1, @RequestParam String param2) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable: %s; param1 - %s; param2 - %s", pathVariable, param1, param2);
    }

}
