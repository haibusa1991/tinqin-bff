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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
        Class<?> testClass;
        try {
            testClass = Class.forName(
                    scanner.findCandidateComponents("com.tinqin.bff")
                            .stream()
                            .map(BeanDefinition::getBeanClassName)
                            .filter(className -> className.toLowerCase().contains("test"))
                            .findFirst()
                            .orElseThrow());
        } catch (Exception e) {
            throw new RuntimeException("Fix me: Test class not present");
        }


        List<RequestMappingData> controllerAnnotations = this.getAnnotatedMethods(testClass)
                .stream()
                .sorted(Comparator.comparing(Method::getName).reversed())
                .map(this::getRequestMappingData)
                .toList();

        RestExportGenerator generator = new RestExportGenerator();

        try {
            generator.generate(controllerAnnotations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
    }

    private RequestMappingData getRequestMappingData(Method method) {
        String classRequestMappingPath = Stream.of(method.getDeclaringClass().getDeclaredAnnotations())
                .filter(e -> e.annotationType().equals(RequestMapping.class))
                .findFirst()
                .map(e -> (RequestMapping) e)
                .map(RequestMapping::path)
                .map(e -> e.length > 0 ? e[0] : "")
                .orElse("");

        System.out.println();
        return RequestMappingData.builder()
                .classRequestMappingPath(classRequestMappingPath)
                .returnType(method.getReturnType())
                .methodName(method.getName())
                .requestMapping(this.getRequestMappingAnnotation(method))
                .parameterAnnotations(method.getParameterAnnotations())
                .parameters(method.getParameters())
                .build();
    }

    private RequestMapping getRequestMappingAnnotation(Method method) {
        List<Class<? extends Annotation>> requestMappings = List.of(
                GetMapping.class,
                PostMapping.class,
                PutMapping.class,
                PatchMapping.class,
                DeleteMapping.class,
                RequestMapping.class
        );

        Annotation annotation = Arrays.stream(method.getDeclaredAnnotations()).filter(e ->
                        requestMappings.contains(e.annotationType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No annotation of type RequestMapping or its aliases is present."));

        return ConvertRequestMapping.from(annotation);
    }

    private List<Method> getAnnotatedMethods(Class<?> c) {

        return Arrays.stream(c.getDeclaredMethods())
                .filter(e -> Arrays.stream(e.getDeclaredAnnotations())
                        .anyMatch(a -> a.annotationType().equals(RestExport.class)))
                .toList();
    }
}
