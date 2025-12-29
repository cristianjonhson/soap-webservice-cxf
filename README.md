# SOAP Web Service - Apache CXF (Contract-First)

Proyecto de ejemplo que demuestra un Web Service SOAP usando Apache CXF con el enfoque **Contract-First** (primero el contrato WSDL, luego el código Java).

## 📋 Características

- ✅ Enfoque **Contract-First**: El WSDL define el contrato del servicio
- ✅ **Apache CXF 3.5.5** como framework SOAP
- ✅ Generación automática de clases Java desde WSDL
- ✅ Spring Framework para configuración
- ✅ Ejemplo funcional de servicio de gestión de clientes
- ✅ Servidor embebido Jetty para pruebas

## 🏗️ Arquitectura

El proyecto sigue el patrón Contract-First:

1. **WSDL** (`CustomerService.wsdl`) - Define el contrato del servicio
2. **Generación de código** - Maven genera las clases Java desde el WSDL
3. **Implementación** - `CustomerServiceImpl` implementa la interfaz generada
4. **Configuración CXF** - Spring configura y expone el servicio

## 📁 Estructura del Proyecto

```
soap-webservice-cxf/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/soap/
│   │   │       └── service/
│   │   │           └── CustomerServiceImpl.java
│   │   ├── resources/
│   │   │   └── CustomerService.wsdl
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           ├── web.xml
│   │           └── cxf-servlet.xml
│   └── test/
│       └── java/
└── target/
    └── generated-sources/
        └── cxf/  (clases generadas automáticamente)
```

## 🚀 Cómo Ejecutar

### Prerrequisitos

- Java 11 o superior
- Maven 3.6+

### Pasos

1. **Generar las clases desde el WSDL:**
   ```bash
   mvn clean generate-sources
   ```

2. **Compilar el proyecto:**
   ```bash
   mvn clean package
   ```

3. **Ejecutar con Jetty:**
   ```bash
   mvn jetty:run
   ```

4. **Acceder al servicio:**
   - WSDL: http://localhost:8080/soap-ws/services/customer?wsdl
   - Endpoint: http://localhost:8080/soap-ws/services/customer

## 🧪 Probar el Servicio

### Usando SOAP UI o Postman

**1. Obtener todos los clientes:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

**2. Obtener un cliente por ID:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getCustomerRequest>
         <id>1</id>
      </soap:getCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**3. Crear un nuevo cliente:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:createCustomerRequest>
         <name>Ana Martínez</name>
         <email>ana.martinez@example.com</email>
         <phone>+34 600 555 777</phone>
      </soap:createCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Usando cURL

```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>'
```

## 📝 Operaciones Disponibles

| Operación | Descripción |
|-----------|-------------|
| `getCustomer` | Obtener un cliente por ID |
| `createCustomer` | Crear un nuevo cliente |
| `getAllCustomers` | Listar todos los clientes |

## 🔧 Tecnologías Utilizadas

- **Apache CXF 3.5.5** - Framework SOAP
- **Spring Framework 5.3.23** - Inyección de dependencias y configuración
- **JAX-WS** - API de Java para Web Services
- **JAXB** - Binding XML-Java
- **Maven** - Gestión de dependencias y build
- **Jetty** - Servidor embebido para desarrollo

## 📚 Conceptos Clave

### Contract-First vs Code-First

Este proyecto usa **Contract-First**:
- ✅ El WSDL es la fuente de verdad
- ✅ Las clases Java se generan automáticamente
- ✅ Garantiza interoperabilidad con otros clientes
- ✅ Cambios en el contrato se reflejan automáticamente en el código

### Generación de Código

El plugin `cxf-codegen-plugin` genera automáticamente:
- Clases de datos (Customer)
- Clases de request/response
- Interfaz del servicio (CustomerServicePortType)
- Service y Port

## 🐳 Despliegue

### Generar WAR

```bash
mvn clean package
```

El WAR se generará en `target/soap-ws.war` y puede desplegarse en:
- Apache Tomcat
- WildFly
- WebLogic
- Jetty standalone

## 📖 Recursos Adicionales

- [Apache CXF Documentation](https://cxf.apache.org/docs/)
- [JAX-WS Specification](https://jakarta.ee/specifications/xml-web-services/)
- [SOAP Tutorial](https://www.w3schools.com/xml/xml_soap.asp)

## 📄 Licencia

MIT License

---

**Desarrollado con ❤️ usando Apache CXF**
