package com.tinqin.bff.restexportprocessor;

import lombok.Builder;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

public class RequestLineBuilder {
    private final RequestMappingData mappingData;

    public RequestLineBuilder(RequestMappingData mappingData) {
        this.mappingData = mappingData;
    }

    public String getRequestLine() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.mappingData.getRequestMapping().method()[0])
                .append(" ")
                .append(this.mappingData.getClassRequestMappingPath());

        if (this.mappingData.getRequestMapping().path().length > 0) {
            sb.append(this.mappingData.getRequestMapping().path()[0]);
        }

//        List<BiTuple<Parameter, RequestParam>> queryParameters = this.annotationHelper.getRequestParams();
        List<MirrorParameter> requestParams = mappingData.getMirrorParameters()
                .stream()
                .filter(e -> e.getAnnotation().annotationType().equals(RequestParam.class))
                .toList();

        if (!requestParams.isEmpty()) {
            sb.append("?");
            sb.append(requestParams.stream()
                    .map(this::composeQueryParameter)
                    .collect(Collectors.joining("&")));
        }

        return sb.toString();
    }

    private String composeQueryParameter(BiTuple<Parameter, RequestParam> query) {

        StringBuilder stringBuilder = new StringBuilder();

        String name = query.getSecondElement().name().isEmpty()
                ? query.getFirstElement().getName()
                : query.getSecondElement().name();

        stringBuilder
                .append(name)
                .append("=")
                .append("{")
                .append(query.getFirstElement().getName())
                .append("}");

        return stringBuilder.toString();
    }

    private String composeQueryParameter(MirrorParameter mirrorParameter) {

        StringBuilder stringBuilder = new StringBuilder();

        String name = ((RequestParam) mirrorParameter.getAnnotation()).name().isEmpty()
                ? mirrorParameter.getName()
                : ((RequestParam) mirrorParameter.getAnnotation()).name();

        stringBuilder
                .append(name)
                .append("=")
                .append("{")
                .append(mirrorParameter.getName())
                .append("}");

        return stringBuilder.toString();
    }
}
