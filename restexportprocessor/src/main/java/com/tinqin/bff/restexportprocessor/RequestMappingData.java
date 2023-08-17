package com.tinqin.bff.restexportprocessor;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

@Builder
@Getter
public class RequestMappingData {

    private String classRequestMappingPath;
    private Class<?> returnType;
    private String methodName;
    private RequestMapping requestMapping;
    private List<MirrorParameter> mirrorParameters;
}
