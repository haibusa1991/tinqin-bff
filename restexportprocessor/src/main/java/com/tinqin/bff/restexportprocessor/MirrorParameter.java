package com.tinqin.bff.restexportprocessor;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;

@Builder
@Getter
public class MirrorParameter {

    private String name;
    private Class<?> parameterType;
    private Annotation annotation;
}
