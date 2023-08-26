package com.tinqin.bff.domain.storageClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tinqin.storage.restexport.StorageRestExport;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RestExportClientFactory {
    private final ApplicationContext context;

    @Value("${STORAGE_CLIENT_NAME}")
    private String storageClientName;
    @Value("${STORAGE_PORT}")
    private String storagePort;

    @Value("${ZOOSTORE_CLIENT_NAME}")
    private String zoostoreClientName;
    @Value("${ZOOSTORE_PORT}")
    private String zoostorePort;

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    StorageRestExport getStorageClient() {

        return Feign.builder()
                .encoder(new JacksonEncoder(context.getBean(ObjectMapper.class)))
                .decoder(new JacksonDecoder(context.getBean(ObjectMapper.class)))
                .target(StorageRestExport.class, String.format("http://%s:%s", storageClientName, storagePort));
    }

    @Bean
    ZooStoreRestExport getZooStoreClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder(context.getBean(ObjectMapper.class)))
                .decoder(new JacksonDecoder(context.getBean(ObjectMapper.class)))
                .target(ZooStoreRestExport.class, String.format("http://%s:%s", zoostoreClientName, zoostorePort));
    }
}