package com.processos.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "location")
@Getter
@Setter
public class LocationProperties  {
	
	private Path base;
	
}
