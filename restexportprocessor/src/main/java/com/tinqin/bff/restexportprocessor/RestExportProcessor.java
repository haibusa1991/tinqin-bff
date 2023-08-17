package com.tinqin.bff.restexportprocessor;
/*
GENERAL NOTES
LIMITATIONS:
   1. Class level annotation @RequestMapping returns the first path only. @RequestMapping(path = {"/path1", "/path2"}) will
   yield "/path1"
   2. @RequestMapping aliases(GET, POST, PATCH, PUT, DELETE) return the first path only. e.g. @GetMapping(path = {"/path1", "/path2"}) will
   yield "/path1"
*/

import com.google.auto.service.AutoService;
import com.helger.jcodemodel.JCodeModelException;
import com.tinqin.bff.restexportprocessor.annotation.RestExport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

//@Component
@RequiredArgsConstructor
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.tinqin.bff.restexportprocessor.annotation.RestExport")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RestExportProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("111111111111111111111111111111\nINITING\n111111111111111111111111111111");
        Element element = roundEnv.getElementsAnnotatedWith(RestExport.class).stream().findFirst().get();
//        roundEnv.getElementsAnnotatedWith(RestExport.class).stream().findFirst().get().getEnclosingElement().getAnnotation(RequestMapping.class).path()
//        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(RestExport.class);
//        roundEnv.getElementsAnnotatedWith(RestExport.class).stream().findFirst().get()
        return true;
    }

    private Optional<RequestMappingData> generateMappingDataFromSymbol(Element element) {
        if (!isInController.test(element)) {
            return Optional.empty();
        }

        element.getEnclosingElement().getAnnotation(RequestMapping)

        RequestMappingData build = RequestMappingData.builder()
                .classRequestMappingPath(e.getEnclosingElement().getAnnotation())
                .returnType()
                .methodName()
                .requestMapping()
                .parameterAnnotations()
                .parameters()
                .build();
    }

    Predicate<Element> isInController = e -> {
        Optional<Controller> controller = Optional.ofNullable(e.getEnclosingElement().getAnnotation(Controller.class));
        Optional<RestController> restController = Optional.ofNullable(e.getEnclosingElement().getAnnotation(RestController.class));

        return controller.isPresent() || restController.isPresent();
    };

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

        Class<?> returnType = method.getReturnType();

        if (method.getGenericReturnType() instanceof ParameterizedType) {
            try {
                returnType = Class.forName(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getTypeName());
            } catch (Exception ignored) {

            }
        }


        System.out.println();
        return RequestMappingData.builder()
                .classRequestMappingPath(classRequestMappingPath)
                .returnType(returnType)
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
