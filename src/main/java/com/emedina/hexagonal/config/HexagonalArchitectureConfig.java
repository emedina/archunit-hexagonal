package com.emedina.hexagonal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Hexagonal Architecture properties.
 * 
 * @author Enrique Medina Montenegro
 */
@Configuration
@EnableConfigurationProperties(HexagonalArchitectureProperties.class)
public class HexagonalArchitectureConfig {
}
