package com.example.soap.service;

import com.example.soap.generated.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del servicio de clientes usando Apache CXF (Contract-First)
 * 
 * Esta clase implementa el servicio SOAP definido en el WSDL.
 * Las clases Java se generan automáticamente desde el WSDL usando cxf-codegen-plugin.
 */
@WebService(
    serviceName = "CustomerService",
    portName = "CustomerServicePort",
    targetNamespace = "http://soap.example.com/",
    endpointInterface = "com.example.soap.generated.CustomerServicePortType"
)
public class CustomerServiceImpl implements CustomerServicePortType {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    // Simulación de base de datos en memoria
    private static final Map<Long, Customer> customerDatabase = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    // Inicializar con datos de ejemplo
    static {
        Customer customer1 = new Customer();
        customer1.setId(idCounter.getAndIncrement());
        customer1.setName("Juan Pérez");
        customer1.setEmail("juan.perez@example.com");
        customer1.setPhone("+34 600 123 456");
        customerDatabase.put(customer1.getId(), customer1);

        Customer customer2 = new Customer();
        customer2.setId(idCounter.getAndIncrement());
        customer2.setName("María García");
        customer2.setEmail("maria.garcia@example.com");
        customer2.setPhone("+34 600 789 012");
        customerDatabase.put(customer2.getId(), customer2);
    }

    @Override
    public GetCustomerResponse getCustomer(GetCustomerRequest parameters) {
        logger.info("Obteniendo cliente con ID: {}", parameters.getId());
        
        GetCustomerResponse response = new GetCustomerResponse();
        Customer customer = customerDatabase.get(parameters.getId());
        
        if (customer == null) {
            logger.warn("Cliente no encontrado con ID: {}", parameters.getId());
            // En un caso real, podrías lanzar una excepción SOAP Fault
            customer = new Customer();
            customer.setId(parameters.getId());
            customer.setName("No encontrado");
            customer.setEmail("N/A");
        }
        
        response.setCustomer(customer);
        return response;
    }

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerRequest parameters) {
        logger.info("Creando nuevo cliente: {}", parameters.getName());
        
        Customer customer = new Customer();
        customer.setId(idCounter.getAndIncrement());
        customer.setName(parameters.getName());
        customer.setEmail(parameters.getEmail());
        customer.setPhone(parameters.getPhone());
        
        customerDatabase.put(customer.getId(), customer);
        
        CreateCustomerResponse response = new CreateCustomerResponse();
        response.setCustomer(customer);
        
        logger.info("Cliente creado exitosamente con ID: {}", customer.getId());
        return response;
    }

    @Override
    public GetAllCustomersResponse getAllCustomers(GetAllCustomersRequest parameters) {
        logger.info("Obteniendo todos los clientes. Total: {}", customerDatabase.size());
        
        GetAllCustomersResponse response = new GetAllCustomersResponse();
        List<Customer> customers = new ArrayList<>(customerDatabase.values());
        response.getCustomers().addAll(customers);
        
        return response;
    }
}
