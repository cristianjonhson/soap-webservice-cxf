package com.example.config;

import com.example.soap.generated.CustomerService;
import com.example.soap.generated.CustomerServicePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del cliente SOAP
 */
@Configuration
public class SoapClientConfig {

    /**
     * URL del servicio SOAP (debe estar corriendo)
     */
    private static final String SOAP_SERVICE_URL = "http://localhost:8080/soap-ws/services/customer";

    @Bean
    public CustomerServicePortType customerServiceClient() {
        CustomerService service = new CustomerService();
        return service.getCustomerServicePort();
    }
}
