# 📚 Explicación Completa: Cómo Funciona el Proyecto SOAP

## 🎯 ¿Qué es SOAP?

**SOAP** (Simple Object Access Protocol) es un protocolo de mensajería basado en XML para intercambiar información estructurada en servicios web. Piensa en SOAP como un "sobre" que envuelve tus datos y los envía a través de HTTP.

### Estructura de un mensaje SOAP:
```xml
<soap:Envelope>          <!-- El sobre -->
   <soap:Header/>        <!-- Información opcional (autenticación, etc) -->
   <soap:Body>           <!-- El contenido real -->
      <operacion>
         <parametros/>
      </operacion>
   </soap:Body>
</soap:Envelope>
```

---

## 🏗️ Arquitectura del Proyecto: Contract-First

Este proyecto usa el enfoque **Contract-First** (primero el contrato):

### 1️⃣ El Contrato (WSDL)
El **WSDL** ([`CustomerService.wsdl`](src/main/resources/CustomerService.wsdl)) define:
- **Qué operaciones** ofrece el servicio (getCustomer, createCustomer, etc.)
- **Qué parámetros** recibe cada operación
- **Qué respuestas** devuelve
- **Dónde está** el servicio (URL)

```xml
<!-- Ejemplo: Definición de una operación -->
<wsdl:operation name="getCustomer">
    <wsdl:input message="tns:getCustomerRequest"/>
    <wsdl:output message="tns:getCustomerResponse"/>
</wsdl:operation>
```

### 2️⃣ Generación Automática de Código
Maven ejecuta el plugin `cxf-codegen-plugin` que:
- Lee el WSDL
- Genera automáticamente clases Java en `target/generated-sources/cxf/`

**Clases generadas:**
- `Customer.java` - La entidad con id, name, email, phone
- `GetCustomerRequest.java` - Petición para obtener cliente
- `GetCustomerResponse.java` - Respuesta con datos del cliente
- `CustomerServicePortType.java` - Interfaz del servicio
- `CustomerService.java` - Service locator
- Y más...

### 3️⃣ Implementación
[`CustomerServiceImpl.java`](src/main/java/com/example/soap/service/CustomerServiceImpl.java) implementa la interfaz generada:

```java
@WebService(
    endpointInterface = "com.example.soap.generated.CustomerServicePortType"
)
public class CustomerServiceImpl implements CustomerServicePortType {
    
    @Override
    public GetCustomerResponse getCustomer(GetCustomerRequest request) {
        // Tu lógica de negocio aquí
        Customer customer = database.get(request.getId());
        
        GetCustomerResponse response = new GetCustomerResponse();
        response.setCustomer(customer);
        return response;
    }
}
```

### 4️⃣ Configuración de CXF
[`cxf-servlet.xml`](src/main/webapp/WEB-INF/cxf-servlet.xml) le dice a CXF:
- Qué clase implementa el servicio
- En qué URL publicarlo
- Qué WSDL usar

```xml
<jaxws:endpoint 
    id="customerService"
    implementor="com.example.soap.service.CustomerServiceImpl"
    address="/customer"
    wsdlLocation="classpath:CustomerService.wsdl">
</jaxws:endpoint>
```

---

## 🔄 Flujo de una Petición

### Ejemplo: Obtener un cliente por ID

```
1. Cliente envía petición HTTP POST
   ↓
   URL: http://localhost:8080/soap-ws/services/customer
   Content-Type: text/xml
   
   <soap:Envelope>
      <soap:Body>
         <getCustomerRequest>
            <id>1</id>
         </getCustomerRequest>
      </soap:Body>
   </soap:Envelope>

2. Apache CXF intercepta la petición
   ↓
   - Valida que el XML sea válido
   - Deserializa el XML a objetos Java
   - Crea un objeto GetCustomerRequest con id=1

3. CXF invoca tu implementación
   ↓
   CustomerServiceImpl.getCustomer(request)

4. Tu código ejecuta la lógica
   ↓
   - Busca en la base de datos (o en memoria)
   - Crea un objeto Customer
   - Lo envuelve en un GetCustomerResponse

5. CXF serializa la respuesta a XML
   ↓
   <soap:Envelope>
      <soap:Body>
         <getCustomerResponse>
            <customer>
               <id>1</id>
               <name>Juan Pérez</name>
               <email>juan.perez@example.com</email>
               <phone>+34 600 123 456</phone>
            </customer>
         </getCustomerResponse>
      </soap:Body>
   </soap:Envelope>

6. Cliente recibe la respuesta
```

---

## 📂 Componentes Clave del Proyecto

### 1. WSDL - El Contrato
**Ubicación:** [`src/main/resources/CustomerService.wsdl`](src/main/resources/CustomerService.wsdl)

Define:
- **Types**: Estructura de datos (Customer, requests, responses)
- **Messages**: Mensajes de entrada/salida
- **PortType**: Operaciones disponibles
- **Binding**: Cómo se transportan los mensajes (SOAP/HTTP)
- **Service**: URL del endpoint

### 2. Implementación del Servicio
**Ubicación:** [`src/main/java/com/example/soap/service/CustomerServiceImpl.java`](src/main/java/com/example/soap/service/CustomerServiceImpl.java)

Contiene la **lógica de negocio**:
```java
// Simula una base de datos en memoria
private static final Map<Long, Customer> customerDatabase = new ConcurrentHashMap<>();

// Implementa las operaciones del WSDL
public GetCustomerResponse getCustomer(GetCustomerRequest request) {
    Customer customer = customerDatabase.get(request.getId());
    // ...
}
```

### 3. Configuración de Spring/CXF
**Ubicación:** [`src/main/webapp/WEB-INF/cxf-servlet.xml`](src/main/webapp/WEB-INF/cxf-servlet.xml)

Conecta todo:
- Define el bean de implementación
- Crea el endpoint SOAP
- Especifica la URL y el WSDL

### 4. Configuración Web
**Ubicación:** [`src/main/webapp/WEB-INF/web.xml`](src/main/webapp/WEB-INF/web.xml)

Configura:
- El servlet de CXF
- El contexto de Spring
- Las URLs mapeadas

### 5. POM.xml - Maven
**Ubicación:** [`pom.xml`](pom.xml)

Define:
- **Dependencias**: Apache CXF, Spring, JAXB
- **Plugin de generación**: `cxf-codegen-plugin` para WSDL→Java
- **Jetty**: Servidor embebido para pruebas

---

## 🔧 Proceso de Compilación y Ejecución

### Paso 1: `mvn clean generate-sources`
```
1. Lee CustomerService.wsdl
2. Ejecuta cxf-codegen-plugin
3. Genera clases Java en target/generated-sources/cxf/
   - Customer.java
   - GetCustomerRequest.java
   - GetCustomerResponse.java
   - CustomerServicePortType.java (interfaz)
   - etc.
```

### Paso 2: `mvn compile`
```
1. Compila las clases generadas
2. Compila tu implementación (CustomerServiceImpl)
3. Verifica que CustomerServiceImpl implemente correctamente la interfaz
```

### Paso 3: `mvn jetty:run`
```
1. Inicia servidor Jetty en puerto 8080
2. Despliega la aplicación web
3. Spring carga cxf-servlet.xml
4. CXF publica el servicio en /soap-ws/services/customer
5. El servicio está listo para recibir peticiones
```

---

## 🎭 Ventajas del Enfoque Contract-First

### ✅ Ventajas:
1. **Interoperabilidad**: Cualquier cliente que entienda el WSDL puede consumir el servicio
2. **Documentación automática**: El WSDL ES la documentación
3. **Validación**: CXF valida automáticamente contra el esquema
4. **Generación de código**: No escribes las clases de datos manualmente
5. **Versionado**: Cambios en el WSDL = nueva versión del contrato

### 🔄 Alternative: Code-First
En el enfoque Code-First:
1. Escribes primero las clases Java
2. CXF genera el WSDL automáticamente
3. Menos control sobre el contrato

**Este proyecto usa Contract-First** porque es la mejor práctica en entornos empresariales.

---

## 💡 Conceptos Clave

### JAX-WS
**Java API for XML Web Services** - El estándar de Java para servicios SOAP.

Anotaciones importantes:
```java
@WebService          // Marca una clase como servicio SOAP
@WebMethod           // Marca un método como operación SOAP (opcional)
@WebParam            // Nombra un parámetro
@WebResult           // Nombra el resultado
```

### JAXB
**Java Architecture for XML Binding** - Convierte entre XML y Java.

CXF usa JAXB para:
- **Unmarshalling**: XML → Objetos Java (petición entrante)
- **Marshalling**: Objetos Java → XML (respuesta saliente)

### Apache CXF
Framework que:
- Implementa JAX-WS
- Maneja toda la infraestructura SOAP
- Proporciona herramientas de generación de código
- Integra con Spring

---

## 🧪 Ejemplo Completo en Acción

### 1. Cliente hace petición:
```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope>
        <soap:Body>
          <createCustomerRequest>
            <name>Ana López</name>
            <email>ana@example.com</email>
          </createCustomerRequest>
        </soap:Body>
      </soap:Envelope>'
```

### 2. CXF procesa:
```
XML → JAXB Unmarshalling → CreateCustomerRequest objeto
```

### 3. Tu código ejecuta:
```java
public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
    Customer customer = new Customer();
    customer.setId(idCounter.getAndIncrement());  // 3
    customer.setName(request.getName());           // "Ana López"
    customer.setEmail(request.getEmail());         // "ana@example.com"
    
    customerDatabase.put(customer.getId(), customer);
    
    CreateCustomerResponse response = new CreateCustomerResponse();
    response.setCustomer(customer);
    return response;
}
```

### 4. CXF responde:
```
CreateCustomerResponse objeto → JAXB Marshalling → XML
```

### 5. Cliente recibe:
```xml
<soap:Envelope>
  <soap:Body>
    <createCustomerResponse>
      <customer>
        <id>3</id>
        <name>Ana López</name>
        <email>ana@example.com</email>
      </customer>
    </createCustomerResponse>
  </soap:Body>
</soap:Envelope>
```

---

## 📊 Diagrama del Flujo Completo

```
┌─────────────┐
│   Cliente   │
│  (Postman,  │
│   SoapUI)   │
└──────┬──────┘
       │ 1. Envía XML SOAP
       │    (HTTP POST)
       ▼
┌─────────────────────────┐
│     Apache CXF          │
│  ┌──────────────────┐   │
│  │ CXFServlet       │   │ 2. Intercepta
│  │ (web.xml)        │   │
│  └────────┬─────────┘   │
│           │             │
│  ┌────────▼─────────┐   │
│  │ JAXB Unmarshall  │   │ 3. XML → Java
│  │ (XML to Object)  │   │
│  └────────┬─────────┘   │
└───────────┼─────────────┘
            │
            ▼
┌──────────────────────────┐
│  CustomerServiceImpl     │ 4. Ejecuta lógica
│  ┌────────────────────┐  │
│  │ getCustomer()      │  │
│  │ createCustomer()   │  │
│  │ getAllCustomers()  │  │
│  └─────────┬──────────┘  │
│            │             │
│  ┌─────────▼──────────┐  │
│  │ Base de datos      │  │ 5. Consulta/Modifica
│  │ (En memoria)       │  │
│  └────────────────────┘  │
└────────────┬─────────────┘
             │
             ▼
┌─────────────────────────┐
│     Apache CXF          │
│  ┌──────────────────┐   │
│  │ JAXB Marshall    │   │ 6. Java → XML
│  │ (Object to XML)  │   │
│  └────────┬─────────┘   │
└───────────┼─────────────┘
            │
            ▼
┌─────────────┐
│   Cliente   │ 7. Recibe respuesta XML
└─────────────┘
```

---

## 🔍 Detalle de Archivos Clave

### CustomerService.wsdl
```xml
<wsdl:definitions>
  <!-- 1. TIPOS: Define Customer, Request, Response -->
  <wsdl:types>
    <xsd:complexType name="Customer">
      <xsd:element name="id" type="xsd:long"/>
      <xsd:element name="name" type="xsd:string"/>
      ...
    </xsd:complexType>
  </wsdl:types>
  
  <!-- 2. MENSAJES: Define mensajes de entrada/salida -->
  <wsdl:message name="getCustomerRequest">...</wsdl:message>
  <wsdl:message name="getCustomerResponse">...</wsdl:message>
  
  <!-- 3. PORT TYPE: Define las operaciones -->
  <wsdl:portType name="CustomerServicePortType">
    <wsdl:operation name="getCustomer">...</wsdl:operation>
    <wsdl:operation name="createCustomer">...</wsdl:operation>
  </wsdl:portType>
  
  <!-- 4. BINDING: Define cómo se transporta (SOAP/HTTP) -->
  <wsdl:binding name="CustomerServiceBinding">...</wsdl:binding>
  
  <!-- 5. SERVICE: Define la URL del endpoint -->
  <wsdl:service name="CustomerService">
    <wsdl:port binding="tns:CustomerServiceBinding">
      <soap:address location="http://localhost:8080/..."/>
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```

### pom.xml - Plugin de Generación
```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <configuration>
                <wsdlOptions>
                    <wsdlOption>
                        <!-- Lee este WSDL -->
                        <wsdl>${basedir}/src/main/resources/CustomerService.wsdl</wsdl>
                        <!-- Genera clases en este paquete -->
                        <extraargs>
                            <extraarg>-p</extraarg>
                            <extraarg>com.example.soap.generated</extraarg>
                        </extraargs>
                    </wsdlOption>
                </wsdlOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### web.xml - Configuración del Servlet
```xml
<!-- 1. Configuración de Spring -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>WEB-INF/cxf-servlet.xml</param-value>
</context-param>

<!-- 2. Listener de Spring -->
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<!-- 3. Servlet de CXF -->
<servlet>
    <servlet-name>CXFServlet</servlet-name>
    <servlet-class>org.apache.cxf.transport.servlet.CXFServlet</servlet-class>
</servlet>

<!-- 4. Mapeo de URL -->
<servlet-mapping>
    <servlet-name>CXFServlet</servlet-name>
    <url-pattern>/services/*</url-pattern>
</servlet-mapping>
```

---

## 🎓 Resumen Ejecutivo

**Este proyecto SOAP funciona así:**

1. 📄 **WSDL define el contrato** - Qué operaciones, parámetros y respuestas
2. ⚙️ **Maven genera clases Java** - Desde el WSDL automáticamente
3. 💻 **Tú implementas la lógica** - En CustomerServiceImpl
4. 🔌 **CXF expone el servicio** - En HTTP con SOAP
5. 🌐 **Clientes consumen** - Enviando XML SOAP
6. 🔄 **CXF traduce** - XML ↔ Java automáticamente

**La magia está en:**
- CXF maneja toda la complejidad de SOAP
- JAXB convierte automáticamente entre XML y Java
- Tú solo te enfocas en la lógica de negocio

---

## 🚀 Comandos Útiles

```bash
# Generar clases desde WSDL
mvn clean generate-sources

# Ver las clases generadas
ls -la target/generated-sources/cxf/com/example/soap/generated/

# Compilar todo
mvn clean compile

# Ejecutar el servicio
mvn jetty:run

# Probar el servicio
./test-soap.sh

# Ver el WSDL publicado
curl http://localhost:8080/soap-ws/services/customer?wsdl
```

---

## 📚 Recursos Adicionales

- **Apache CXF**: https://cxf.apache.org/
- **JAX-WS Tutorial**: https://docs.oracle.com/javaee/7/tutorial/jaxws.htm
- **SOAP Specification**: https://www.w3.org/TR/soap/
- **WSDL Specification**: https://www.w3.org/TR/wsdl/

---

**¿Preguntas?** Revisa:
- [README.md](README.md) - Guía de inicio rápido
- [postman/README-Postman.md](postman/README-Postman.md) - Guía de Postman
- [soapui/README-SoapUI.md](soapui/README-SoapUI.md) - Guía de SoapUI
