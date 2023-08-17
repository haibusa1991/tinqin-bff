package com.tinqin.bff.core.restExport;

import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AnnotationHelper {
    private final Parameter[] parameters;
    private final Annotation[][] annotations;

    @Builder
    public AnnotationHelper(Parameter[] parameters, Annotation[][] annotations) {
        this.parameters = parameters;
        this.annotations = annotations;
    }

    public List<BiTuple<Parameter, RequestParam>> getRequestParams() {
        List<BiTuple<Parameter, RequestParam>> queryParameters = new ArrayList<>();
        for (int i = 0; i < this.parameters.length; i++) {
            Optional<Annotation> requestParam = Arrays.stream(this.annotations[i])
                    .filter(e -> e.annotationType().equals(RequestParam.class))
                    .findFirst();

            if (requestParam.isPresent()) {
                queryParameters.add(BiTuple.<Parameter, RequestParam>builder()
                        .firstElement(this.parameters[i])
                        .secondElement((RequestParam) requestParam.get())
                        .build());
            }
        }

        return queryParameters;
    }

    public List<BiTuple<Parameter, PathVariable>> getPathVariables() {
        List<BiTuple<Parameter, PathVariable>> pathVariables = new ArrayList<>();
        for (int i = 0; i < this.parameters.length; i++) {
            Optional<Annotation> requestParam = Arrays.stream(this.annotations[i])
                    .filter(e -> e.annotationType().equals(PathVariable.class))
                    .findFirst();

            if (requestParam.isPresent()) {
                pathVariables.add(BiTuple.<Parameter, PathVariable>builder()
                        .firstElement(this.parameters[i])
                        .secondElement((PathVariable) requestParam.get())
                        .build());
            }
        }

        return pathVariables;
    }

    public  List<BiTuple<Parameter, RequestBody>> getRequestBody(){

        List<BiTuple<Parameter, RequestBody>> requestBody = new ArrayList<>();
        for (int i = 0; i < this.parameters.length; i++) {
            Optional<Annotation> requestParam = Arrays.stream(this.annotations[i])
                    .filter(e -> e.annotationType().equals(RequestBody.class))
                    .findFirst();

            if (requestParam.isPresent()) {
                requestBody.add(BiTuple.<Parameter, RequestBody>builder()
                        .firstElement(this.parameters[i])
                        .secondElement((RequestBody) requestParam.get())
                        .build());
            }
        }

        return requestBody;
    }
}
