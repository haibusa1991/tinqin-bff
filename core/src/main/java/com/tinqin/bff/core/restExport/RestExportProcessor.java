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

import com.helger.jcodemodel.JCodeModelException;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RestExportProcessor {
    private final ApplicationContext applicationContext;


    @PostConstruct
    public void findAnnotations() throws JCodeModelException {
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
        List<Method> annotatedMethods = this.getAnnotatedMethods(voucherClass).stream().sorted(Comparator.comparing(Method::getName).reversed()).toList();

        Method method = annotatedMethods.get(0);


        RequestMappingData.builder()
                .returnType(method.getReturnType())
                .methodName(method.getName())
                .httpMethod(this.getHttpMethod(method))
                .pathValue()

        RequestMappingData httpMethod = this.getHttpMethod(method);
        Class<?> returnType = ;
        String methodSignature = this.getMethodSignature(method);
        GetMethodProcessor processor = new GetMethodProcessor();
        String simpleMethod = processor.getSimpleMethod(requestMappingPath, httpMethod, returnType, methodSignature);

        System.out.println();
    }

    private ControllerHttpMethod getHttpMethod(Method method) {
        List<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations()).toList();

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(GetMapping.class))) {
            return ControllerHttpMethod.GET;
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PostMapping.class))) {
            return ControllerHttpMethod.POST;
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PatchMapping.class))) {
            return ControllerHttpMethod.PATCH;
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PutMapping.class))) {
            return ControllerHttpMethod.PUT;
        }

        if (annotations.stream().anyMatch(e -> e.annotationType().equals(DeleteMapping.class))) {
            return ControllerHttpMethod.DELETE;
        }

        throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
    }

//    private RequestMappingData getHttpMethod(Method method) {
//        List<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations()).toList();
//
//        if (annotations.stream().anyMatch(e -> e.annotationType().equals(GetMapping.class))) {
//            return this.getRequestMappingData(annotations, GetMapping.class);
//        }
//
//        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PostMapping.class))) {
//            return this.getRequestMappingData(annotations, PostMapping.class);
//        }
//
//        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PatchMapping.class))) {
//            return this.getRequestMappingData(annotations, PatchMapping.class);
//        }
//
//        if (annotations.stream().anyMatch(e -> e.annotationType().equals(PutMapping.class))) {
//            return this.getRequestMappingData(annotations, PutMapping.class);
//        }
//
//        if (annotations.stream().anyMatch(e -> e.annotationType().equals(DeleteMapping.class))) {
//            return this.getRequestMappingData(annotations, DeleteMapping.class);
//        }
//
//        throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
//    }

//    private RequestMappingData getRequestMappingData(List<Annotation> methodAnnotations, Class<?> httpMethodAnnotationClass) {
//        Annotation annotation = methodAnnotations.stream().filter(e -> e.annotationType().equals(httpMethodAnnotationClass)).findFirst().orElseThrow();
//
//        Map<Class<? extends Annotation>, ControllerHttpMethod> methods = Map.of(
//                GetMapping.class, ControllerHttpMethod.GET,
//                PostMapping.class, ControllerHttpMethod.POST,
//                PutMapping.class, ControllerHttpMethod.PUT,
//                PatchMapping.class, ControllerHttpMethod.PATCH,
//                DeleteMapping.class, ControllerHttpMethod.DELETE
//        );
//
//        String path;
//        switch (httpMethodAnnotationClass.getName()) {
//            case "org.springframework.web.bind.annotation.GetMapping" ->
//                    path = ((GetMapping) annotation).path().length > 0 ? ((GetMapping) annotation).path()[0] : "";
//            case "org.springframework.web.bind.annotation.PostMapping" ->
//                    path = ((PostMapping) annotation).path().length > 0 ? ((PostMapping) annotation).path()[0] : "";
//            case "org.springframework.web.bind.annotation.PutMapping" ->
//                    path = ((PutMapping) annotation).path().length > 0 ? ((PutMapping) annotation).path()[0] : "";
//            case "org.springframework.web.bind.annotation.PatchMapping" ->
//                    path = ((PatchMapping) annotation).path().length > 0 ? ((PatchMapping) annotation).path()[0] : "";
//            case "org.springframework.web.bind.annotation.DeleteMapping" ->
//                    path = ((DeleteMapping) annotation).path().length > 0 ? ((DeleteMapping) annotation).path()[0] : "";
//            default -> throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
//        }
//
//        return RequestMappingData.builder()
//                .methodAnnotation(methods.get(httpMethodAnnotationClass))
//                .pathValue(path)
//                .build();
//    }

    private String getRequestMappingPath(List<Annotation> methodAnnotations, Class<?> requestMappingAliasClass) {
        Annotation annotation = methodAnnotations.stream().filter(e -> e.annotationType().equals(requestMappingAliasClass)).findFirst().orElseThrow();

        Map<Class<? extends Annotation>, ControllerHttpMethod> methods = Map.of(
                GetMapping.class, ControllerHttpMethod.GET,
                PostMapping.class, ControllerHttpMethod.POST,
                PutMapping.class, ControllerHttpMethod.PUT,
                PatchMapping.class, ControllerHttpMethod.PATCH,
                DeleteMapping.class, ControllerHttpMethod.DELETE
        );

        String path;
        switch (requestMappingAliasClass.getName()) {
            case "org.springframework.web.bind.annotation.GetMapping" ->
                    path = ((GetMapping) annotation).path().length > 0 ? ((GetMapping) annotation).path()[0] : "";
            case "org.springframework.web.bind.annotation.PostMapping" ->
                    path = ((PostMapping) annotation).path().length > 0 ? ((PostMapping) annotation).path()[0] : "";
            case "org.springframework.web.bind.annotation.PutMapping" ->
                    path = ((PutMapping) annotation).path().length > 0 ? ((PutMapping) annotation).path()[0] : "";
            case "org.springframework.web.bind.annotation.PatchMapping" ->
                    path = ((PatchMapping) annotation).path().length > 0 ? ((PatchMapping) annotation).path()[0] : "";
            case "org.springframework.web.bind.annotation.DeleteMapping" ->
                    path = ((DeleteMapping) annotation).path().length > 0 ? ((DeleteMapping) annotation).path()[0] : "";
            default -> throw new IllegalArgumentException("Http method not recognized or using @RequestMapping.");
        }

        return path;
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

    private Class<RequestMapping> convertToRequestMapping(GetMapping getMapping) throws NoSuchFieldException, IllegalAccessException {

        Class<RequestMapping> requestMappingClass = RequestMapping.class;

        Field name = requestMappingClass.getDeclaredField("name");
        name.setAccessible(true);
        name.set(String.class,getMapping.name());

        Field value = requestMappingClass.getDeclaredField("value");
        value.setAccessible(true);
        value.set(String[].class,getMapping.path());

        Field path = requestMappingClass.getDeclaredField("path");
        path.setAccessible(true);
        path.set(String[].class,getMapping.value());

        Field method = requestMappingClass.getDeclaredField("method");
        method.setAccessible(true);
        method.set(RequestMethod[].class, new RequestMethod[]{RequestMethod.GET});

        Field params = requestMappingClass.getDeclaredField("params");
        params.setAccessible(true);
        params.set(String[].class,getMapping.params());

        Field headers = requestMappingClass.getDeclaredField("headers");
        headers.setAccessible(true);
        headers.set(String[].class,getMapping.params());

        Field consumes = requestMappingClass.getDeclaredField("consumes");
        consumes.setAccessible(true);
        consumes.set(String[].class,getMapping.consumes());

        Field produces = requestMappingClass.getDeclaredField("produces");
        produces.setAccessible(true);
        produces.set(String[].class,getMapping.produces());

        return requestMappingClass;
    }

    private Class<RequestMapping> convertToRequestMapping(PostMapping postMapping) throws NoSuchFieldException, IllegalAccessException {

        Class<RequestMapping> requestMappingClass = RequestMapping.class;

        Field name = requestMappingClass.getDeclaredField("name");
        name.setAccessible(true);
        name.set(String.class,postMapping.name());

        Field value = requestMappingClass.getDeclaredField("value");
        value.setAccessible(true);
        value.set(String[].class,postMapping.path());

        Field path = requestMappingClass.getDeclaredField("path");
        path.setAccessible(true);
        path.set(String[].class,postMapping.value());

        Field method = requestMappingClass.getDeclaredField("method");
        method.setAccessible(true);
        method.set(RequestMethod[].class, new RequestMethod[]{RequestMethod.POST});

        Field params = requestMappingClass.getDeclaredField("params");
        params.setAccessible(true);
        params.set(String[].class,postMapping.params());

        Field headers = requestMappingClass.getDeclaredField("headers");
        headers.setAccessible(true);
        headers.set(String[].class,postMapping.params());

        Field consumes = requestMappingClass.getDeclaredField("consumes");
        consumes.setAccessible(true);
        consumes.set(String[].class,postMapping.consumes());

        Field produces = requestMappingClass.getDeclaredField("produces");
        produces.setAccessible(true);
        produces.set(String[].class,postMapping.produces());

        return requestMappingClass;
    }
}
