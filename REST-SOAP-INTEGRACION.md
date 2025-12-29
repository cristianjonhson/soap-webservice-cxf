# 🔄 Integración REST ↔ SOAP

## 🎯 Respuesta Corta

**Sí, ambos pueden interactuar entre sí.** Necesitas un intermediario que haga la traducción entre formatos.

- ✅ **REST puede consumir SOAP** - Actúa como cliente SOAP
- ✅ **SOAP puede consumir REST** - Actúa como cliente HTTP/REST
- ⚙️ **Requiere conversión** - JSON ↔ XML

---

## 📊 Diferencias Fundamentales

| Aspecto | REST | SOAP |
|---------|------|------|
| **Formato** | JSON (ligero) | XML (verboso) |
| **Protocolo** | HTTP GET/POST/PUT/DELETE | HTTP POST con XML |
| **Headers** | `Content-Type: application/json` | `Content-Type: text/xml` |
| **Estructura** | Libre, flexible | Fija: `<soap:Envelope>` |
| **Verbos HTTP** | Usa GET, POST, PUT, DELETE | Solo POST |
| **URL** | RESTful: `/customers/1` | Endpoint único: `/services/customer` |
| **Contrato** | Swagger/OpenAPI (opcional) | WSDL (obligatorio) |
| **Parsing** | JSON simple | XML + validación XSD |

---

## 🔄 Escenario 1: REST Consume SOAP

Un servicio **REST** puede llamar a un servicio **SOAP** actuando como cliente SOAP.

### Diagrama de Flujo

```
┌──────────────┐         ┌─────────────────┐         ┌──────────────┐
│ Cliente REST │         │   API REST      │         │ Servicio     │
│              │         │   (Gateway)     │         │   SOAP       │
│              │         │                 │         │              │
└──────┬───────┘         └────────┬────────┘         └──────┬───────┘
       │                          │                         │
       │ 1. GET /customers/1      │                         │
       │    (JSON esperado)       │                         │
       │─────────────────────────>│                         │
       │                          │                         │
       │                          │ 2. Crea petición SOAP   │
       │                          │    <getCustomerRequest> │
       │                          │    <id>1</id>           │
       │                          │    (XML)                │
       │                          │────────────────────────>│
       │                          │                         │
       │                          │                         │ 3. Procesa
       │                          │                         │    XML
       │                          │                         │
       │                          │ 4. Respuesta SOAP       │
       │                          │    <getCustomerResponse>│
       │                          │    <customer>...</>     │
       │                          │<────────────────────────│
       │                          │    (XML)                │
       │                          │                         │
       │                          │ 5. Convierte XML → JSON │
       │                          │                         │
       │ 6. Respuesta REST        │                         │
       │    {"id": 1, "name"...}  │                         │
       │<─────────────────────────│                         │
       │    (JSON)                │                         │
       │                          │                         │
```

### Código de Ejemplo: Gateway REST → SOAP

```java
package com.example.rest.gateway;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.soap.generated.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerRestGateway {
    
    private CustomerServicePortType soapClient;
    
    public CustomerRestGateway() {
        // Inicializa cliente SOAP
        CustomerService service = new CustomerService();
        this.soapClient = service.getCustomerServicePort();
    }
    
    // REST GET endpoint que consume SOAP internamente
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomer(@PathVariable Long id) {
        try {
            // 1. Crea petición SOAP
            GetCustomerRequest soapRequest = new GetCustomerRequest();
            soapRequest.setId(id);
            
            // 2. Llama al servicio SOAP (envía XML)
            GetCustomerResponse soapResponse = soapClient.getCustomer(soapRequest);
            
            // 3. Convierte respuesta SOAP (XML) a JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            Customer customer = soapResponse.getCustomer();
            
            if (customer != null) {
                jsonResponse.put("id", customer.getId());
                jsonResponse.put("name", customer.getName());
                jsonResponse.put("email", customer.getEmail());
                jsonResponse.put("phone", customer.getPhone());
            }
            
            // 4. Devuelve JSON al cliente REST
            return ResponseEntity.ok(jsonResponse);
            
        } catch (Exception e) {
            // Manejo de errores
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // REST POST endpoint que consume SOAP internamente
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(
            @RequestBody Map<String, String> requestBody) {
        try {
            // 1. Extrae datos del JSON
            String name = requestBody.get("name");
            String email = requestBody.get("email");
            String phone = requestBody.get("phone");
            
            // 2. Crea petición SOAP
            CreateCustomerRequest soapRequest = new CreateCustomerRequest();
            soapRequest.setName(name);
            soapRequest.setEmail(email);
            soapRequest.setPhone(phone);
            
            // 3. Llama al servicio SOAP
            CreateCustomerResponse soapResponse = soapClient.createCustomer(soapRequest);
            
            // 4. Convierte respuesta a JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            Customer customer = soapResponse.getCustomer();
            jsonResponse.put("id", customer.getId());
            jsonResponse.put("name", customer.getName());
            jsonResponse.put("email", customer.getEmail());
            jsonResponse.put("phone", customer.getPhone());
            jsonResponse.put("message", "Customer created successfully");
            
            return ResponseEntity.status(201).body(jsonResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // REST GET endpoint para obtener todos los clientes
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers() {
        try {
            // 1. Crea petición SOAP
            GetAllCustomersRequest soapRequest = new GetAllCustomersRequest();
            
            // 2. Llama al servicio SOAP
            GetAllCustomersResponse soapResponse = soapClient.getAllCustomers(soapRequest);
            
            // 3. Convierte lista de clientes a JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("customers", soapResponse.getCustomers());
            jsonResponse.put("total", soapResponse.getCustomers().size());
            
            return ResponseEntity.ok(jsonResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
```

### Prueba del Gateway REST → SOAP

**Request REST (JSON):**
```bash
# Obtener cliente
curl http://localhost:8080/api/v1/customers/1

# Crear cliente
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana López",
    "email": "ana@example.com",
    "phone": "+34 600 999 888"
  }'

# Obtener todos los clientes
curl http://localhost:8080/api/v1/customers
```

**Response REST (JSON):**
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+34 600 123 456"
}
```

**Pero internamente se envió esto al SOAP (XML):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getCustomerRequest>
      <id>1</id>
    </getCustomerRequest>
  </soap:Body>
</soap:Envelope>
```

**Y se recibió esto del SOAP (XML):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getCustomerResponse>
      <customer>
        <id>1</id>
        <name>Juan Pérez</name>
        <email>juan@example.com</email>
        <phone>+34 600 123 456</phone>
      </customer>
    </getCustomerResponse>
  </soap:Body>
</soap:Envelope>
```

**Conversión automática:** El gateway convirtió XML → JSON transparentemente.

---

## 🔄 Escenario 2: SOAP Consume REST

Un servicio **SOAP** puede llamar a un servicio **REST** usando cliente HTTP.

### Diagrama de Flujo

```
┌──────────────┐         ┌─────────────────┐         ┌──────────────┐
│ Cliente SOAP │         │  Servicio SOAP  │         │   API REST   │
│              │         │   (Gateway)     │         │              │
│              │         │                 │         │              │
└──────┬───────┘         └────────┬────────┘         └──────┬───────┘
       │                          │                         │
       │ 1. SOAP Request          │                         │
       │    <getCustomerRequest>  │                         │
       │    <id>1</id>            │                         │
       │    (XML)                 │                         │
       │─────────────────────────>│                         │
       │                          │                         │
       │                          │ 2. Crea petición HTTP   │
       │                          │    GET /customers/1     │
       │                          │    Accept: application/json
       │                          │────────────────────────>│
       │                          │                         │
       │                          │                         │ 3. Procesa
       │                          │                         │    y devuelve
       │                          │                         │    JSON
       │                          │                         │
       │                          │ 4. Respuesta REST       │
       │                          │    {"id": 1, "name"...} │
       │                          │<────────────────────────│
       │                          │    (JSON)               │
       │                          │                         │
       │                          │ 5. Convierte JSON → XML │
       │                          │                         │
       │ 6. SOAP Response         │                         │
       │    <getCustomerResponse> │                         │
       │    <customer>...</>      │                         │
       │<─────────────────────────│                         │
       │    (XML)                 │                         │
       │                          │                         │
```

### Código de Ejemplo: SOAP → REST

```java
package com.example.soap.service;

import javax.jws.WebService;
import com.example.soap.generated.*;
import java.net.http.*;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@WebService(
    serviceName = "CustomerService",
    portName = "CustomerServicePort",
    targetNamespace = "http://soap.example.com/",
    endpointInterface = "com.example.soap.generated.CustomerServicePortType"
)
public class CustomerServiceSoapToRest implements CustomerServicePortType {
    
    private static final String REST_API_URL = "https://api.example.com/customers";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public CustomerServiceSoapToRest() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public GetCustomerResponse getCustomer(GetCustomerRequest request) {
        try {
            // 1. Crea petición HTTP GET al servicio REST
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(REST_API_URL + "/" + request.getId()))
                .header("Accept", "application/json")
                .GET()
                .build();
            
            // 2. Envía petición y recibe respuesta JSON
            HttpResponse<String> response = httpClient.send(
                httpRequest, 
                HttpResponse.BodyHandlers.ofString()
            );
            
            // 3. Parsea el JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonData = objectMapper.readValue(
                response.body(), 
                Map.class
            );
            
            // 4. Convierte JSON a objetos SOAP
            Customer soapCustomer = new Customer();
            soapCustomer.setId(((Number) jsonData.get("id")).longValue());
            soapCustomer.setName((String) jsonData.get("name"));
            soapCustomer.setEmail((String) jsonData.get("email"));
            soapCustomer.setPhone((String) jsonData.get("phone"));
            
            // 5. Crea respuesta SOAP
            GetCustomerResponse soapResponse = new GetCustomerResponse();
            soapResponse.setCustomer(soapCustomer);
            
            // 6. Devuelve XML al cliente SOAP
            return soapResponse;
            
        } catch (Exception e) {
            throw new RuntimeException("Error calling REST API: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        try {
            // 1. Crea el payload JSON desde datos SOAP
            Map<String, String> jsonPayload = Map.of(
                "name", request.getName(),
                "email", request.getEmail(),
                "phone", request.getPhone()
            );
            String jsonBody = objectMapper.writeValueAsString(jsonPayload);
            
            // 2. Crea petición HTTP POST al servicio REST
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(REST_API_URL))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            
            // 3. Envía petición
            HttpResponse<String> response = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
            );
            
            // 4. Parsea respuesta JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonData = objectMapper.readValue(
                response.body(),
                Map.class
            );
            
            // 5. Convierte a objetos SOAP
            Customer soapCustomer = new Customer();
            soapCustomer.setId(((Number) jsonData.get("id")).longValue());
            soapCustomer.setName((String) jsonData.get("name"));
            soapCustomer.setEmail((String) jsonData.get("email"));
            soapCustomer.setPhone((String) jsonData.get("phone"));
            
            // 6. Crea respuesta SOAP
            CreateCustomerResponse soapResponse = new CreateCustomerResponse();
            soapResponse.setCustomer(soapCustomer);
            
            return soapResponse;
            
        } catch (Exception e) {
            throw new RuntimeException("Error calling REST API: " + e.getMessage(), e);
        }
    }
    
    @Override
    public GetAllCustomersResponse getAllCustomers(GetAllCustomersRequest request) {
        try {
            // 1. Crea petición HTTP GET
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(REST_API_URL))
                .header("Accept", "application/json")
                .GET()
                .build();
            
            // 2. Envía petición
            HttpResponse<String> response = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
            );
            
            // 3. Parsea array JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonData = objectMapper.readValue(
                response.body(),
                Map.class
            );
            
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> customersList = 
                (java.util.List<Map<String, Object>>) jsonData.get("customers");
            
            // 4. Convierte a lista de objetos SOAP
            GetAllCustomersResponse soapResponse = new GetAllCustomersResponse();
            
            for (Map<String, Object> item : customersList) {
                Customer customer = new Customer();
                customer.setId(((Number) item.get("id")).longValue());
                customer.setName((String) item.get("name"));
                customer.setEmail((String) item.get("email"));
                customer.setPhone((String) item.get("phone"));
                soapResponse.getCustomers().add(customer);
            }
            
            return soapResponse;
            
        } catch (Exception e) {
            throw new RuntimeException("Error calling REST API: " + e.getMessage(), e);
        }
    }
}
```

### Dependencias Necesarias (pom.xml)

```xml
<!-- Para hacer peticiones HTTP a REST -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

---

## 🛠️ Patrón de Diseño: Gateway/Adapter

### Gateway Pattern

El **Gateway** es el patrón más usado para integrar REST ↔ SOAP.

```
┌─────────────────────────────────────────────────┐
│              Gateway/Adapter Layer              │
│                                                 │
│  ┌───────────────┐         ┌────────────────┐  │
│  │  REST Client  │         │  SOAP Client   │  │
│  │  (Consume     │         │  (Consume      │  │
│  │   SOAP)       │         │   REST)        │  │
│  └───────┬───────┘         └────────┬───────┘  │
│          │                          │          │
│          │                          │          │
│  ┌───────▼──────────────────────────▼───────┐  │
│  │          Conversion Layer                │  │
│  │                                          │  │
│  │   • JSON ↔ XML                           │  │
│  │   • REST ↔ SOAP                          │  │
│  │   • Error handling                       │  │
│  │   • Logging                              │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
                    │
        ┌───────────┴────────────┐
        │                        │
        ▼                        ▼
┌─────────────┐          ┌─────────────┐
│  Servicio   │          │  Servicio   │
│    SOAP     │          │    REST     │
│  (Legacy)   │          │  (Moderno)  │
└─────────────┘          └─────────────┘
```

### Beneficios del Gateway

1. **Desacoplamiento** - Los servicios no se conocen entre sí
2. **Conversión centralizada** - Un solo lugar para lógica de conversión
3. **Logging y monitoreo** - Punto único para auditoría
4. **Seguridad** - Autenticación/autorización centralizada
5. **Versionado** - Manejo de diferentes versiones de APIs

---

## 🎯 Casos de Uso Reales

### Caso 1: Modernización de Legacy

```
Empresa tiene:
- Sistemas antiguos con SOAP (años 2000-2010)
- Nuevas apps móviles que usan REST/JSON

Solución:
→ Gateway REST que consume SOAP internamente
→ Apps móviles usan REST moderno
→ Backend legacy sigue con SOAP sin cambios
```

### Caso 2: Integración B2B

```
Tu empresa (REST) debe integrarse con:
- Banco (SOAP)
- Proveedor logística (SOAP)
- Sistema fiscal gobierno (SOAP)

Solución:
→ Tu servicio REST consume servicios SOAP externos
→ Conversion layer maneja XML ↔ JSON
```

### Caso 3: Migración Gradual

```
Migración de SOAP a REST:
- Fase 1: Gateway REST wrapper sobre SOAP
- Fase 2: Clientes migran a REST
- Fase 3: Reemplazar SOAP backend por REST
- Fase 4: Deprecar SOAP completamente

Gateway permite migración sin romper clientes existentes
```

---

## 📊 Tabla Comparativa de Integración

| Escenario | Complejidad | Uso Común | Herramientas |
|-----------|-------------|-----------|--------------|
| **REST → SOAP** | Media | Consumir legacy SOAP | Cliente SOAP generado, Spring RestTemplate |
| **SOAP → REST** | Baja | Integrar con APIs modernas | HttpClient, Jackson/Gson |
| **Gateway REST/SOAP** | Alta | Exponer SOAP como REST | Spring Boot, Apache Camel |
| **ESB** | Muy Alta | Integraciones empresariales | MuleSoft, WSO2, Apache ServiceMix |

---

## 🔧 Ejemplo Completo: Proyecto con Ambos

### Estructura del Proyecto

```
soap-rest-integration/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       ├── rest/
│       │       │   └── CustomerRestController.java  ← REST API
│       │       ├── soap/
│       │       │   └── CustomerSoapService.java     ← SOAP Service
│       │       └── gateway/
│       │           ├── RestToSoapGateway.java       ← REST → SOAP
│       │           └── SoapToRestGateway.java       ← SOAP → REST
│       └── resources/
│           ├── application.properties
│           └── CustomerService.wsdl
└── README.md
```

### Configuración Spring Boot (application.properties)

```properties
# Servidor
server.port=8080

# SOAP Service endpoint
soap.service.url=http://localhost:8080/soap-ws/services/customer

# REST API externa
rest.api.url=https://jsonplaceholder.typicode.com/users

# Logging
logging.level.com.example=DEBUG
```

### Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Web (REST) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Apache CXF (SOAP) -->
    <dependency>
        <groupId>org.apache.cxf</groupId>
        <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
        <version>3.5.5</version>
    </dependency>
    
    <!-- Jackson (JSON parsing) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    
    <!-- HttpClient (para SOAP → REST) -->
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
    </dependency>
</dependencies>
```

---

## ⚠️ Consideraciones Importantes

### Performance

```
REST → SOAP → REST
  ↓      ↓      ↓
JSON → XML → JSON

Cada conversión añade latencia:
- Parsing JSON: ~5ms
- Parsing XML: ~15ms
- Total overhead: ~20-40ms por request
```

**Recomendación:** Usar cache cuando sea posible.

### Manejo de Errores

```java
// REST maneja errores con HTTP Status
return ResponseEntity.status(404).body("Not found");

// SOAP maneja errores con SOAP Fault
throw new SOAPFaultException(...);

// Gateway debe traducir entre ambos
try {
    // Llama SOAP
} catch (SOAPFaultException e) {
    // Convierte a error REST
    return ResponseEntity.status(500).body(
        Map.of("error", e.getMessage())
    );
}
```

### Autenticación

```
REST usa:
- Bearer tokens (JWT)
- API Keys
- OAuth 2.0

SOAP usa:
- WS-Security
- SAML tokens
- Basic Auth en headers

Gateway debe manejar ambos sistemas
```

---

## 🎓 Resumen Ejecutivo

### ✅ Lo Que Funciona

| Integración | ¿Funciona? | Complejidad | Cuando Usar |
|-------------|-----------|-------------|-------------|
| REST consume SOAP | ✅ Sí | Media | Consumir servicios legacy |
| SOAP consume REST | ✅ Sí | Baja | Integrar con APIs modernas |
| Gateway REST wrapper | ✅ Sí | Alta | Exponer SOAP como REST |
| Gateway SOAP wrapper | ✅ Sí | Alta | Exponer REST como SOAP |

### 🔑 Puntos Clave

1. **Ambos usan HTTP** - Base común para integración
2. **Conversión necesaria** - JSON ↔ XML en el gateway
3. **Gateway pattern** - Mejor práctica para integración
4. **Overhead de conversión** - Considerar performance
5. **Manejo de errores** - Traducir entre formatos de error

### 💡 Recomendaciones

**Para proyectos nuevos:**
- ✅ Usa REST/JSON (más simple, moderno)
- ❌ Evita SOAP a menos que sea requerido

**Para sistemas legacy:**
- ✅ Crea gateway REST sobre SOAP existente
- ✅ Migra gradualmente cliente por cliente
- ✅ Mantén SOAP funcionando hasta migración completa

**Para integraciones:**
- ✅ Usa cliente SOAP cuando sea necesario
- ✅ Centraliza conversiones en gateway
- ✅ Implementa retry logic y circuit breakers

---

## 📚 Referencias

- [COMO-FUNCIONA.md](COMO-FUNCIONA.md) - Arquitectura SOAP
- [SOAP-XML-EXPLICACION.md](SOAP-XML-EXPLICACION.md) - Detalles XML en SOAP
- [XSD-vs-XML.md](XSD-vs-XML.md) - XSD vs XML
- [Spring Boot + CXF](https://cxf.apache.org/docs/springboot.html) - Integración Spring
- [REST API Best Practices](https://restfulapi.net/) - Diseño REST
- [Apache Camel](https://camel.apache.org/) - Enterprise Integration Patterns
