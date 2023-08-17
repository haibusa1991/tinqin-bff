package com.tinqin.bff.core.restExport;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.JCMWriter;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.*;

public class RestExportGenerator {

    public void generate(List<RequestMappingData> methodList) throws IOException, JCodeModelException {
        this.process(methodList);
    }

    private void process(List<RequestMappingData> RestExportAnnotatedMethods) throws JCodeModelException, IOException {
        JCodeModel jcm = new JCodeModel();

        JDefinedClass clazz = jcm._class("com.tinqin.bff.core.restExport.GeneratedRestExport", EClassType.INTERFACE);
        clazz.annotate(feign.Headers.class).paramArray("value", "Content-Type: application/json"); //hardcoded

        RestExportAnnotatedMethods.forEach(e -> this.addMethod(clazz, e));

        JCMWriter writer = new JCMWriter(jcm);
        writer.build(new File("core/src/main/java"));
        System.out.println();
    }

    private void addMethod(JDefinedClass c, RequestMappingData mappingData) {
        RequestLineBuilder requestLineBuilder = RequestLineBuilder.builder().mappingData(mappingData).build();
        AnnotationHelper annotationHelper = AnnotationHelper.builder()
                .annotations(mappingData.getParameterAnnotations())
                .parameters(mappingData.getParameters())
                .build();

        JMethod method = c.method(JMod.NONE, mappingData.getReturnType(), mappingData.getMethodName());

        method.annotate(RequestLine.class).param("value", requestLineBuilder.getRequestLine());

        annotationHelper.getPathVariables().forEach(e -> this.addPathVariableParameterToMethod(method, e));
        annotationHelper.getRequestParams().forEach(e -> this.addQueryVariableParameterToMethod(method, e));
        annotationHelper.getRequestBody().forEach(e -> this.addRequestBodyParameterToMethod(method, e));
    }

    private void addPathVariableParameterToMethod(JMethod method, BiTuple<Parameter, PathVariable> pathVariables) {
        Parameter parameter = pathVariables.getFirstElement();
        PathVariable pathVariable = pathVariables.getSecondElement();

        String parameterName = pathVariable.name().isEmpty()
                ? parameter.getName()
                : pathVariable.name();

        method.param(parameter.getType(), parameterName)
                .annotate(Param.class)
                .param("value", parameterName);
    }

    private void addQueryVariableParameterToMethod(JMethod method, BiTuple<Parameter, RequestParam> paramVariable) {

        Parameter parameter = paramVariable.getFirstElement();
        RequestParam pathVariable = paramVariable.getSecondElement();

        String parameterName = pathVariable.name().isEmpty()
                ? parameter.getName()
                : pathVariable.name();

        method.param(parameter.getType(), parameterName)
                .annotate(Param.class)
                .param("value", parameter.getName());
    }

    private void addRequestBodyParameterToMethod(JMethod method, BiTuple<Parameter, RequestBody> paramVariable) {

        Parameter parameter = paramVariable.getFirstElement();

        method.param(parameter.getType(), parameter.getName())
                .annotate(Param.class);
    }


}
