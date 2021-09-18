package com.processos;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.processos.config.LocationProperties;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableConfigurationProperties(LocationProperties.class)
public class JobsComSpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobsComSpringBatchApplication.class, args);
	}



}
