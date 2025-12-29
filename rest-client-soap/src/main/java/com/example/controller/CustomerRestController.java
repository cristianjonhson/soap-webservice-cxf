package com.example.controller;

import com.example.soap.generated.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller que actúa como Gateway hacia el servicio SOAP
 */
@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "*")
public class CustomerRestController {

    private final CustomerServicePortType soapClient;

    @Autowired
    public CustomerRestController(CustomerServicePortType soapClient) {
        this.soapClient = soapClient;
        System.out.println("✅ REST Controller inicializado - Cliente SOAP conectado");
    }

    /**
     * GET /api/v1/customers/health - Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        System.out.println("📥 REST Request: GET /api/v1/customers/health");
        
        Map<String, Object> health = new HashMap<>();
        health.put("service", "REST Gateway to SOAP");
        health.put("status", "UP");
        
        try {
            // Test SOAP connection
            GetAllCustomersRequest request = new GetAllCustomersRequest();
            soapClient.getAllCustomers(request);
            health.put("soap_connection", "OK");
            System.out.println("✅ Health Check: SOAP connection OK");
        } catch (Exception e) {
            health.put("soap_connection", "ERROR: " + e.getMessage());
            health.put("status", "DOWN");
            System.out.println("❌ Health Check: SOAP connection FAILED");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
        
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    /**
     * GET /api/v1/customers - Obtener todos los clientes
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCustomers() {
        System.out.println("📥 REST Request: GET /api/v1/customers");
        System.out.println("🔄 Llamando al servicio SOAP...");
        
        try {
            GetAllCustomersRequest soapRequest = new GetAllCustomersRequest();
            GetAllCustomersResponse soapResponse = soapClient.getAllCustomers(soapRequest);
            
            List<Map<String, Object>> customers = new ArrayList<>();
            
            for (Customer customer : soapResponse.getCustomers()) {
                Map<String, Object> customerMap = new HashMap<>();
                customerMap.put("id", customer.getId());
                customerMap.put("name", customer.getName());
                customerMap.put("email", customer.getEmail());
                customerMap.put("phone", customer.getPhone());
                customerMap.put("source", "SOAP Service");
                customers.add(customerMap);
            }
            
            System.out.println("✅ REST Response: " + customers.size() + " clientes");
            return ResponseEntity.ok(customers);
            
        } catch (Exception e) {
            System.out.println("❌ Error llamando a SOAP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/customers/{id} - Obtener cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long id) {
        System.out.println("📥 REST Request: GET /api/v1/customers/" + id);
        System.out.println("🔄 Llamando al servicio SOAP...");
        
        try {
            GetCustomerRequest soapRequest = new GetCustomerRequest();
            soapRequest.setId(id);
            
            GetCustomerResponse soapResponse = soapClient.getCustomer(soapRequest);
            Customer customer = soapResponse.getCustomer();
            
            if (customer == null || customer.getId() == 0) {
                System.out.println("❌ Cliente no encontrado: " + id);
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("id", customer.getId());
            customerMap.put("name", customer.getName());
            customerMap.put("email", customer.getEmail());
            customerMap.put("phone", customer.getPhone());
            customerMap.put("source", "SOAP Service");
            
            System.out.println("✅ REST Response: " + customerMap);
            return ResponseEntity.ok(customerMap);
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/v1/customers - Crear nuevo cliente
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody Map<String, String> customerData) {
        System.out.println("📥 REST Request: POST /api/v1/customers");
        System.out.println("   Body: " + customerData);
        System.out.println("🔄 Llamando al servicio SOAP para crear cliente...");
        
        try {
            CreateCustomerRequest soapRequest = new CreateCustomerRequest();
            soapRequest.setName(customerData.get("name"));
            soapRequest.setEmail(customerData.get("email"));
            soapRequest.setPhone(customerData.get("phone"));
            
            CreateCustomerResponse soapResponse = soapClient.createCustomer(soapRequest);
            Customer createdCustomer = soapResponse.getCustomer();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cliente creado exitosamente");
            response.put("id", createdCustomer.getId());
            response.put("name", createdCustomer.getName());
            response.put("email", createdCustomer.getEmail());
            response.put("phone", createdCustomer.getPhone());
            response.put("source", "SOAP Service");
            
            System.out.println("✅ REST Response: Cliente creado con ID " + createdCustomer.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error creando cliente");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
