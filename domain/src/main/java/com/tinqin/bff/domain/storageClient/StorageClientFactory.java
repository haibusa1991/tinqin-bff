package com.tinqin.bff.domain.storageClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqin.storage.restexport.StorageItemRestExport;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class StorageClientFactory {

    @Bean
    StorageItemRestExport getMyRestExportClient() {
        final ObjectMapper objectMapper = new ObjectMapper();

        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(StorageItemRestExport.class, "http://localhost:8080");
    }

}