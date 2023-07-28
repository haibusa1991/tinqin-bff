package com.tinqin.bff.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = "com.tinqin.bff")
//@EntityScan(basePackages = "com.tinqin.bff.persistence.entity")
//@EnableJpaRepositories(basePackages = "com.tinqin.bff.persistence.repository")
@SpringBootApplication
public class TinqinBffApplication {
	public static void main(String[] args) {
		SpringApplication.run(TinqinBffApplication.class, args);
	}
}
