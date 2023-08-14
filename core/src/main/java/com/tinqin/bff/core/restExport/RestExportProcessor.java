package com.tinqin.bff.core.restExport;
/*
GENERAL NOTES
LIMITATIONS:
   1. Class level annotation @RequestMapping returns the first path only. @RequestMapping(path = {"/path1", "/path2"}) will
   yield "/path1"

   2. Method level annotation @RequestMapping not supported, only and its aliases(GET, POST, PATCH, PUT, DELETE).
   3. @RequestMapping aliases(GET, POST, PATCH, PUT, DELETE) return the first path only. e.g. @GetMapping(path = {"/path1", "/path2"}) will
   yield "/path1"
*/

import com.tinqin.bff.api.annotations.RestExport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RestExportProcessor {
    private final ApplicationContext applicationContext;


    @PostConstruct
    public void findAnnotations() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

//        String beanClassName = scanner.findCandidateComponents("com.tinqin.bff")
//                .stream()
//                .map(BeanDefinition::getBeanClassName)
//                .filter(className -> className.contains("Voucher")) //todo find all controllers
//                .findFirst()
//                .orElseThrow();
//
        Class<?> voucherClass = Object.class;
        try {
            voucherClass = Class.forName(
                    scanner.findCandidateComponents("com.tinqin.bff")
                            .stream()
                            .map(BeanDefinition::getBeanClassName)
                            .filter(className -> className.toLowerCase().contains("test"))
                            .findFirst()
                            .orElseThrow());
        } catch (Exception ignored) {
        }


        String requestMappingPath = this.getRequestMappingPaths(voucherClass);
        List<Method> annotatedMethods = this.getAnnotatedMethods(voucherClass);

        Method method = annotatedMethods.get(5);
        RequestMappingData httpMethod = this.getHttpMethod(method);
        Class<?> returnType = method.getReturnType();
        String methodSignature = this.getMethodSignature(method);

        System.out.println();
    }

    private RequestMappingData getHttpMethod(Method method)  {
        List<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations()).toList();

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(GetMapping.class))) {
            return this.getRequestMappingData(annotations, GetMapping.class);
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PostMapping.class))) {
            return this.getRequestMappingData(annotations, PostMapping.class);
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PatchMapping.class))) {
            return this.getRequestMappingData(annotations, PatchMapping.class);
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PutMapping.class))) {
            return this.getRequestMappingData(annotations, PutMapping.class);
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(DeleteMapping.class))) {
            return this.getRequestMappingData(annotations, DeleteMapping.class);
        }

        throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
    }

    private RequestMappingData getRequestMappingData(List<Annotation> methodAnnotations, Class<?> httpMethodAnnotationClass) {
        Annotation annotation = methodAnnotations.stream().filter(e -> e.annotationType().equals(httpMethodAnnotationClass)).findFirst().orElseThrow();

        Map<Class<? extends Annotation>, MethodAnnotation> methods = Map.of(
                GetMapping.class, MethodAnnotation.GET,
                PostMapping.class, MethodAnnotation.POST,
                PutMapping.class, MethodAnnotation.PUT,
                PatchMapping.class, MethodAnnotation.PATCH,
                DeleteMapping.class, MethodAnnotation.DELETE
        );

        String path;
        switch (httpMethodAnnotationClass.getName()) {
            case "org.springframework.web.bind.annotation.GetMapping" -> path = ((GetMapping) annotation).path()[0];
            case "org.springframework.web.bind.annotation.PostMapping" -> path = ((PostMapping) annotation).path()[0];
            case "org.springframework.web.bind.annotation.PutMapping" -> path = ((PutMapping) annotation).path()[0];
            case "org.springframework.web.bind.annotation.PatchMapping" -> path = ((PatchMapping) annotation).path()[0];
            case "org.springframework.web.bind.annotation.DeleteMapping" -> path = ((DeleteMapping) annotation).path()[0];
            default -> throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
        }

        return RequestMappingData.builder()
                .methodAnnotation(methods.get(httpMethodAnnotationClass))
                .pathValue(path)
                .build();
    }

    private List<Method> getAnnotatedMethods(Class<?> c) {

        return Arrays.stream(c.getDeclaredMethods())
                .filter(e -> Arrays.stream(e.getDeclaredAnnotations())
                        .anyMatch(a -> a.annotationType().equals(RestExport.class)))
                .toList();
    }

    private String getRequestMappingPaths(Class<?> c) {

        return Arrays.stream(c.getDeclaredAnnotations())
                .filter(e -> e.annotationType().equals(RequestMapping.class))
                .map(e -> ((RequestMapping) e))
                .map(RequestMapping::path)
                .flatMap(Stream::of)
                .toArray(String[]::new)[0];
    }

    private String getMethodSignature(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();
        String name = method.getName();

        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        for (int i = 0; i < parameters.length; i++) {
            sb.append(this.getAnnotationMethodPair(parameterAnnotations[i][0], parameters[i]));
        }
        sb.append(")");

        return sb.toString();
    }

    private String getAnnotationName(Annotation annotation) {
        String name = annotation.annotationType().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private String getAnnotationMethodPair(Annotation annotation, Parameter parameter) {
        StringBuilder stringBuilder = new StringBuilder();

        if (annotation != null) {
            stringBuilder
                    .append("@")
                    .append(annotation.annotationType().getName())
                    .append(" ");
        }

        stringBuilder
                .append(parameter.getType().getName())
                .append(" ")
                .append(parameter.getName());

        return stringBuilder.toString();
    }
}
