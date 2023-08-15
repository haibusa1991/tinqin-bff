package com.tinqin.bff.core.restExport;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.JCMWriter;
import feign.Param;
import feign.RequestLine;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;

public class GetMethodProcessor {

    public String getSimpleMethod(String requestMappingPath, RequestMappingData mappingData, Class<?> returnType, String methodSignature) throws JCodeModelException {
        this.test(returnType);
//        StringBuilder stringBuilder = new StringBuilder("@RequestLine(\"")
//                .append(mappingData.getMethodAnnotation().toString())
//                .append(" ")
//                .append(requestMappingPath)
//                .append(mappingData.getPathValue())
//                .append("\")")
//                .append(System.lineSeparator());
//
//        stringBuilder
//                .append(returnType.getName())
//                .append(" ")
//                .append(methodSignature);
//
//        return stringBuilder.toString();
        return null;
    }

    private void test(Class<?> returnType) throws JCodeModelException, IOException {
        JCodeModel jcm = new JCodeModel();

        JDefinedClass clazz = jcm._class("com.tinqin.bff.core.restExport.GeneratedRestExport",EClassType.INTERFACE);
        JAnnotationUse annotate1 = clazz.annotate(feign.Headers.class); //hardcoded
        annotate1.paramArray("value","Content-Type: application/json"); //hardcoded

        JMethod myMethod = clazz.method(JMod.NONE, returnType, "myMethod");
        JAnnotationUse annotate = myMethod.annotate(RequestLine.class);
        annotate.param("value","feignRequestGoesHere");
        JVar param1 = myMethod.param(String.class, "param1");
        param1.annotate(Param.class);

        JCMWriter writer = new JCMWriter(jcm);
        writer.build(new File("core/src/main/java"));

    }

}
