package com.tinqin.bff.rest.controller;

import com.tinqin.restexport.annotation.RestExport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.zip.ZipEntry;

@RestController
@RequestMapping(path = "/test")
public class TestRestExportClass {

    @GetMapping(path = "/unannotated")
    private String unannotatedMethod() {
        return "unannotated get";
    }

    @RestExport
    @GetMapping(path = "/response-entity")
    private ResponseEntity<String> responseEntityGet() {
        return ResponseEntity.ok("ResponseEntity Get");
    }

//    @RestExport
//    @RequestMapping(method = RequestMethod.POST)
//    private String requestMappingGet() {
//        return "request mapping get";
//    }
//
//    @RestExport
//    @GetMapping
//    private String simpleGet() {
//        return "simple get";
//    }
//
//    @RestExport
//    @GetMapping(path = "/subpath")
//    private String getWithSubPath() {
//        return "getWithSubPath";
//    }
//
//    @RestExport
//    @GetMapping(path = "/{pathVariable}")
//    private String getWithPathVariable(@PathVariable String pathVariable) {
//        return "getWithPathVariable - " + pathVariable;
//    }
//
//
//    @RestExport
//    @GetMapping
//    private String getWithOneQueryParam(@RequestParam String param1) {
//        return "getWithOneQueryParam - " + param1;
//    }
//
//    @RestExport
//    @GetMapping
//    private String getWithMultipleQueryParam(@RequestParam String param1, @RequestParam @Valid String param2) {
//        return String.format("getWithMultipleQueryParam - %s; %s", param1, param2);
//    }
//
//    @RestExport
//    @GetMapping(path = "/{pathVariable}")
//    private String getWithPathVariableAndOneQueryParam(@PathVariable String pathVariable, @RequestParam String param1) {
//        return String.format("getWithPathVariableAndOneQueryParam - pathVariable: %s; param - %s", pathVariable, param1);
//    }
//
//    @RestExport
//    @GetMapping(path = "/{pathVariable}")
//    private String getWithPathVariableAndMultipleQueryParam(@PathVariable String pathVariable, @RequestParam(name = "customName") String param1, @RequestParam String param2) {
//        return String.format("getWithPathVariableAndOneQueryParam - pathVariable: %s; param1 - %s; param2 - %s", pathVariable, param1, param2);
//    }
//
    @RestExport
    @GetMapping(path = "/{customNamePathVariable}{pathVariable2}")
    private String getWithMultiplePathVariablesAndMultipleQueryParam(@PathVariable(name = "customNamePathVariable") String pathVariable1, @PathVariable String pathVariable2, @RequestParam(name = "customQueryParameterName") String param1, @RequestParam String param2) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable1: %s; pathVariable2: %s; param1 - %s; param2 - %s", pathVariable1, pathVariable2, param1, param2);
    }

    @RestExport
    @PostMapping(path = "/{customNamePathVariable}{pathVariable2}")
    private String postWithMultiplePathVariablesAndMultipleQueryParam(@PathVariable(name = "customNamePathVariable") String pathVariable1,
                                                                     @PathVariable String pathVariable2,
                                                                     @RequestParam(name = "customQueryParameterName") String param1,
                                                                     @RequestParam String param2,
                                                                     @RequestBody ZipEntry myPojoTest) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable1: %s; pathVariable2: %s; param1 - %s; param2 - %s", pathVariable1, pathVariable2, param1, param2);
    }

    @RestExport
    @DeleteMapping(path = "/{customNamePathVariable}{pathVariable2}")
    private String deleteWithMultiplePathVariablesAndMultipleQueryParam(@PathVariable(name = "customNamePathVariable") String pathVariable1,
                                                                     @PathVariable String pathVariable2,
                                                                     @RequestParam(name = "customQueryParameterName") String param1,
                                                                     @RequestParam String param2,
                                                                     @RequestBody ZipEntry myPojoTest) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable1: %s; pathVariable2: %s; param1 - %s; param2 - %s", pathVariable1, pathVariable2, param1, param2);
    }

    @RestExport
    @PatchMapping(path = "/{customNamePathVariable}{pathVariable2}")
    private String patchWithMultiplePathVariablesAndMultipleQueryParam(@PathVariable(name = "customNamePathVariable") String pathVariable1,
                                                                     @PathVariable String pathVariable2,
                                                                     @RequestParam(name = "customQueryParameterName") String param1,
                                                                     @RequestParam String param2,
                                                                     @RequestBody ZipEntry myPojoTest) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable1: %s; pathVariable2: %s; param1 - %s; param2 - %s", pathVariable1, pathVariable2, param1, param2);
    }

    @RestExport
    @PutMapping(path = "/{customNamePathVariable}{pathVariable2}")
    private String putWithMultiplePathVariablesAndMultipleQueryParam(@PathVariable(name = "customNamePathVariable") String pathVariable1,
                                                                     @PathVariable String pathVariable2,
                                                                     @RequestParam(name = "customQueryParameterName") String param1,
                                                                     @RequestParam String param2,
                                                                     @RequestBody ZipEntry myPojoTest) {
        return String.format("getWithPathVariableAndOneQueryParam - pathVariable1: %s; pathVariable2: %s; param1 - %s; param2 - %s", pathVariable1, pathVariable2, param1, param2);
    }


}
