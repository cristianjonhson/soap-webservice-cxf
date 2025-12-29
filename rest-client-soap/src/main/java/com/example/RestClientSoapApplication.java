package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot - REST API que consume servicio SOAP
 */
@SpringBootApplication
public class RestClientSoapApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestClientSoapApplication.class, args);
    }
}
