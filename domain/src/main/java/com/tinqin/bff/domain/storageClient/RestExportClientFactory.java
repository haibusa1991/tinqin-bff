package com.tinqin.bff.domain.storageClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tinqin.storage.restexport.StorageItemRestExport;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@RequiredArgsConstructor
public class RestExportClientFactory {
    private final ApplicationContext context;

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    StorageItemRestExport getStorageClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder(context.getBean(ObjectMapper.class)))
                .decoder(new JacksonDecoder(context.getBean(ObjectMapper.class)))
                .target(StorageItemRestExport.class, "http://localhost:8081");
    }

    @Bean
    ZooStoreRestExport getZooStoreClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder(context.getBean(ObjectMapper.class)))
                .decoder(new JacksonDecoder(context.getBean(ObjectMapper.class)))
                .target(ZooStoreRestExport.class, "http://localhost:8080");
    }
}