package com.tinqin.bff.restexportprocessor;
/*
GENERAL NOTES
LIMITATIONS:
   1. Class level annotation @RequestMapping returns the first path only. @RequestMapping(path = {"/path1", "/path2"}) will
   yield "/path1"
   2. @RequestMapping aliases(GET, POST, PATCH, PUT, DELETE) return the first path only. e.g. @GetMapping(path = {"/path1", "/path2"}) will
   yield "/path1"
*/

import com.google.auto.service.AutoService;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@RequiredArgsConstructor
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.tinqin.restexport.annotation.RestExport")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RestExportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        com.tinqin.restexport.RestExportProcessor processor = new com.tinqin.restexport.RestExportProcessor(roundEnv,
                "rest/src/main/java",
                "com.tinqin.bff.rest.restexport.RestExport");

        processor.processAnnotation();
        return true;
    }
}
