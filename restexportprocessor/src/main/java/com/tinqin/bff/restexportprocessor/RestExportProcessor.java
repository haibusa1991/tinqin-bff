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
import com.tinqin.bff.restexportprocessor.annotation.RestExport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.tinqin.bff.restexportprocessor.annotation.RestExport")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RestExportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedMethod = roundEnv.getElementsAnnotatedWith(RestExport.class);

        if (annotatedMethod.isEmpty()) {
            return true;
        }

        List<RequestMappingData> requestMappingData = annotatedMethod.stream()
                .map(this::generateMappingData)
                .filter(Objects::nonNull)
                .toList();

        RestExportGenerator generator = new RestExportGenerator(requestMappingData);

        try {
            generator.generate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private RequestMappingData generateMappingData(Element element) {
        if (!isInController(element)) {
            return null;
        }

        String[] path = element.getEnclosingElement().getAnnotation(RequestMapping.class).path();
        Class<?> returnTypeClass;

        TypeMirror returnType = ((ExecutableElement) element).getReturnType();

        String targetClassName = returnType.toString();

        Optional<? extends TypeMirror> genericClass = ((DeclaredType) returnType)
                .getTypeArguments()
                .stream()
                .findFirst();

        try {
            returnTypeClass = Class.forName(genericClass.isEmpty() ? targetClassName : genericClass.get().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String methodName = element.getSimpleName().toString();
        List<MirrorParameter> mirrorParameters = ((ExecutableElement) element).getParameters().stream().map(this::getMirrorParameter).toList();

        return RequestMappingData.builder()
                .classRequestMappingPath(path.length > 0 ? path[0] : "")
                .returnType(returnTypeClass)
                .methodName(methodName)
                .requestMapping(ConvertRequestMapping.from(element))
                .mirrorParameters(mirrorParameters)
                .build();
    }

    private Boolean isInController(Element element) {
        Optional<Controller> controller = Optional.ofNullable(element.getEnclosingElement().getAnnotation(Controller.class));
        Optional<RestController> restController = Optional.ofNullable(element.getEnclosingElement().getAnnotation(RestController.class));

        return controller.isPresent() || restController.isPresent();
    }

    public MirrorParameter getMirrorParameter(VariableElement element) {

        Class<?> parameterType;
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
                .name(element.getSimpleName().toString())
                .parameterType(parameterType)
                .annotation(annotation)
                .build();
    }

}
