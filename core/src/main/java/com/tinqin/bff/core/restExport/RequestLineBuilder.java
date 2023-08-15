package com.tinqin.bff.core.restExport;

import lombok.Builder;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

public class RequestLineBuilder {
    private final RequestMappingData mappingData;
    private final AnnotationHelper annotationHelper;

    @Builder
    public RequestLineBuilder(RequestMappingData mappingData) {
        this.mappingData = mappingData;
        this.annotationHelper = AnnotationHelper.builder()
                .annotations(mappingData.getParameterAnnotations())
                .parameters(mappingData.getParameters())
                .build();
    }

    public String getRequestLine() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.mappingData.getRequestMapping().method()[0])
                .append(" ")
                .append(this.mappingData.getClassRequestMappingPath());

        if (this.mappingData.getRequestMapping().path().length > 0) {
            sb.append(this.mappingData.getRequestMapping().path()[0]);
        }

        List<BiTuple<Parameter, RequestParam>> queryParameters = this.annotationHelper.getRequestParams();
        if (!queryParameters.isEmpty()) {
            sb.append("?");
            sb.append(queryParameters.stream()
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
}
