package com.tinqin.bff.core.restExport;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

@Builder
@Getter
public class RequestMappingData {

    private Class<?> returnType;
    private String methodName;
    private ControllerHttpMethod httpMethod;
    private String pathValue;
    private Annotation[][] annotations;
    private Parameter[] parameters;
}
