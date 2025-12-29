# SOAP Web Service - Apache CXF (Contract-First)

> **📦 Estructura del Repositorio**: Este repositorio contiene DOS proyectos independientes:
> - **🟦 Servidor SOAP** (raíz): Puerto 8080 - Servicio SOAP con Apache CXF
> - **🟩 Cliente REST** ([rest-client-soap/](rest-client-soap/)): Puerto 9090 - API REST que consume el SOAP
> 
> Ver [REPOSITORIO.md](REPOSITORIO.md) para documentación completa de ambos proyectos.

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

1. **Generar las clases desde el WSDL y compilar:**
   ```bash
   mvn clean generate-sources compile
   ```

2. **Ejecutar con Jetty:**
   ```bash
   mvn jetty:run
   ```

3. **Acceder al servicio:**
   - **WSDL**: http://localhost:8080/soap-ws/services/customer?wsdl
   - Endpoint: http://localhost:8080/soap-ws/services/customer

> ⚠️ **Nota**: Si accedes al endpoint sin `?wsdl` desde el navegador, verás un error SOAP. Esto es normal. Para consumir el servicio correctamente, usa peticiones SOAP POST o accede al WSDL agregando `?wsdl` al final de la URL.

### Script de Prueba Rápida

Ejecuta el script incluido para probar todas las operaciones:
```bash
./test-soap.sh
```

## 🧪 Probar el Servicio

### Usando SoapUI

#### 1. Crear nuevo proyecto SOAP
1. Abre **SoapUI**
2. Ve a **File → New SOAP Project**
3. En **Initial WSDL**, pega: `http://localhost:8080/soap-ws/services/customer?wsdl`
4. Haz clic en **OK**

#### 2. Probar las operaciones
SoapUI genera automáticamente peticiones de ejemplo para cada operación:

- **getAllCustomers**: Click derecho → Show Request Editor → Ejecutar
- **getCustomer**: Modifica el `<id>1</id>` y ejecuta
- **createCustomer**: Llena los datos del cliente y ejecuta

#### 3. Ejemplo de petición en SoapUI
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

---

### Usando Postman

#### 1. Configurar petición POST
1. Abre **Postman**
2. Crea una nueva petición **POST**
3. URL: `http://localhost:8080/soap-ws/services/customer`

#### 2. Configurar Headers
Agrega estos headers:
- `Content-Type`: `text/xml; charset=utf-8`
- `SOAPAction`: (vacío o la acción específica)

#### 3. Configurar Body
Selecciona **Body → raw → XML** y pega una petición:

**Obtener todos los clientes:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

**Obtener cliente por ID:**
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

**Crear nuevo cliente:**
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

#### 4. Ejecutar
- Haz clic en **Send**
- La respuesta XML aparecerá abajo

#### 5. Importar colección pre-configurada
📦 **Colección lista para usar**: [`postman/SOAP-CustomerService.postman_collection.json`](postman/SOAP-CustomerService.postman_collection.json)

En Postman:
1. Click en **Import**
2. Selecciona el archivo de la colección
3. ¡Listo! Todas las peticiones están configuradas

📖 **Guía completa**: Ver [`postman/README-Postman.md`](postman/README-Postman.md)

📖 **Guía para SoapUI**: Ver [`soapui/README-SoapUI.md`](soapui/README-SoapUI.md)

---

### Ejemplos con cURL

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
