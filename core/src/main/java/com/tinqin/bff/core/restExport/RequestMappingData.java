package com.tinqin.bff.core.restExport;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

@Builder
@Getter
public class RequestMappingData {

    private Class<?> returnType;
    private String methodName;
    private RequestMapping requestMapping;
    private Annotation[][] parameterAnnotations;
    private Parameter[] parameters;
}
