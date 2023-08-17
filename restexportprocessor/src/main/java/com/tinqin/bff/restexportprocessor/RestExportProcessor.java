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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.tinqin.bff.restexportprocessor.annotation.RestExport")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RestExportProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        System.out.println("111111111111111111111111111111\nINITING\n111111111111111111111111111111");
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(RestExport.class);

        if(elementsAnnotatedWith.isEmpty()){
            return true;
        }
        try {

            List<RequestMappingData> requestMappingData = elementsAnnotatedWith.stream()
                    .map(this::generateMappingData)
                    .filter(Objects::nonNull)
                    .toList();

            RestExportGenerator generator = new RestExportGenerator();

            try {
                generator.generate(requestMappingData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private RequestMappingData generateMappingData(Element element) {
        if (!isInController(element)) {
            return null;
        }

        String[] path = element.getEnclosingElement().getAnnotation(RequestMapping.class).path();
        Class<?> returnType = null; //TODO handle generics
        try {
            returnType = Class.forName(((ExecutableElement) element).getReturnType().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String methodName = element.getSimpleName().toString();
        RequestMapping requestMapping = getRequestMappingAnnotation(element);
        List<MirrorParameter> mirrorParameters = ((ExecutableElement) element).getParameters().stream().map(this::getMirrorParameter).toList();

        return RequestMappingData.builder()
                .classRequestMappingPath(path.length > 0 ? path[0] : "")
                .returnType(returnType)
                .methodName(methodName)
                .requestMapping(requestMapping)
                .mirrorParameters(mirrorParameters)
                .build();
    }

    private Boolean isInController(Element element) {
        Optional<Controller> controller = Optional.ofNullable(element.getEnclosingElement().getAnnotation(Controller.class));
        Optional<RestController> restController = Optional.ofNullable(element.getEnclosingElement().getAnnotation(RestController.class));

        return controller.isPresent() || restController.isPresent();
    }

    private RequestMapping getRequestMappingAnnotation(Element element) {
        List<Class<? extends Annotation>> requestMappings = List.of(
                GetMapping.class,
                PostMapping.class,
                PutMapping.class,
                PatchMapping.class,
                DeleteMapping.class,
                RequestMapping.class
        );

        Annotation annotation = requestMappings.stream()
                .map(element::getAnnotation)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No annotation of type RequestMapping or its aliases is present."));

        return ConvertRequestMapping.from(annotation);
    }

    public MirrorParameter getMirrorParameter(VariableElement element) {
        String name = element.getSimpleName().toString();
        Class<?> parameterType = null;
        try {
            parameterType = Class.forName(element.asType().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Annotation annotation = Stream.of(element.getAnnotation(RequestBody.class),
                        element.getAnnotation(PathVariable.class),
                        element.getAnnotation(RequestParam.class))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unannotated parameter in controller signature."));

        return MirrorParameter.builder()
                .name(name)
                .parameterType(parameterType)
                .annotation(annotation)
                .build();
    }

}
