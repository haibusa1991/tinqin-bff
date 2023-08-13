package com.tinqin.bff.core.restExport;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@RequiredArgsConstructor
public class RestExportProcessor {
private final ApplicationContext applicationContext;


    @PostConstruct
    public void findAnnotations(){
        //this.applicationContext.getBeansWithAnnotation(Controller.class).values().stream().filter(e->e.getClass().toString().contains("VoucherController")).findFirst().get().getClass()

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
//for (BeanDefinition beanDefinition : scanner.findCandidateComponents("com.xxx.yyy.controllers")){
//    System.out.println(beanDefinition.getBeanClassName());
        scanner.findCandidateComponents("com.tinqin.bff").stream().findFirst().get().getClass();

        System.out.println();
    }
}
