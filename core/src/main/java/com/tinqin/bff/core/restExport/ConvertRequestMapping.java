package com.tinqin.bff.core.restExport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;

public class ConvertRequestMapping {

    public static RequestMapping from(Annotation requestMappingAlias){

        switch (requestMappingAlias.annotationType().getName()){
            case "org.springframework.web.bind.annotation.RequestMapping" -> {
                return (RequestMapping) requestMappingAlias;
            }
            case "org.springframework.web.bind.annotation.GetMapping" -> {
                return ConvertRequestMapping.fromGetMapping((GetMapping) requestMappingAlias);
            }
            default -> throw new RuntimeException("Not a RequestMapping alias!");
        }
    }

    private static RequestMapping fromGetMapping(GetMapping getMapping) {
            return new RequestMapping(){

                @Override
                public Class<? extends Annotation> annotationType() {
                    return RequestMapping.class;
                }

                @Override
                public String name() {
                    return getMapping.name();
                }

                @Override
                public String[] value() {
                    return getMapping.value();
                }

                @Override
                public String[] path() {
                    return getMapping.path();
                }

                @Override
                public RequestMethod[] method() {
                    return new RequestMethod[]{RequestMethod.GET};
                }

                @Override
                public String[] params() {
                    return getMapping.params();
                }

                @Override
                public String[] headers() {
                    return getMapping.headers();
                }

                @Override
                public String[] consumes() {
                    return getMapping.consumes();
                }

                @Override
                public String[] produces() {
                    return getMapping.produces();
                }
            };
    }
}
