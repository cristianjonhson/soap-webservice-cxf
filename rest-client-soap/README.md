# REST API Client - Consume SOAP Service

Proyecto **independiente** que expone un REST API consumiendo un servicio SOAP externo.

## 📋 Arquitectura

```
Cliente HTTP/JSON  →  REST API (puerto 9090)  →  SOAP Service (puerto 8080)
                      Spring Boot                  Apache CXF
```

## 🚀 Prerequisitos

1. El servicio SOAP debe estar corriendo en `http://localhost:8080/soap-ws/services/customer`
2. Java 11+
3. Maven 3.6+

## ⚙️ Construcción

### 1. Iniciar el servicio SOAP (proyecto separado)

```bash
cd "../Web Service SOAP"
mvn jetty:run
```

### 2. Generar clases desde WSDL y compilar

```bash
mvn clean compile
```

Este comando:
- Descarga el WSDL del servicio SOAP corriendo
- Genera las clases Java del cliente SOAP
- Compila el proyecto

### 3. Ejecutar la aplicación REST

```bash
mvn spring-boot:run
```

El servidor REST iniciará en `http://localhost:9090`

## 📡 Endpoints REST

### Health Check
```bash
curl http://localhost:9090/api/v1/customers/health
```

### Obtener todos los clientes
```bash
curl http://localhost:9090/api/v1/customers
```

### Obtener cliente por ID
```bash
curl http://localhost:9090/api/v1/customers/1
```

### Crear nuevo cliente
```bash
curl -X POST http://localhost:9090/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carlos López",
    "email": "carlos@example.com",
    "phone": "+34 600 111 222"
  }'
```

## 🔄 Flujo de Datos

1. Cliente hace request HTTP/JSON al REST API (puerto 9090)
2. `CustomerRestController` recibe el request
3. Controller convierte JSON → objetos Java
4. Llama al cliente SOAP generado (`CustomerServicePortType`)
5. Cliente SOAP envía request SOAP/XML al servicio (puerto 8080)
6. Servicio SOAP procesa y responde SOAP/XML
7. Cliente SOAP recibe y deserializa la respuesta
8. Controller convierte objetos Java → JSON
9. Responde al cliente HTTP con JSON

## 📂 Estructura del Proyecto

```
rest-client-soap/
├── pom.xml                                    # Maven config
├── src/main/java/com/example/
│   ├── RestClientSoapApplication.java         # Main Spring Boot
│   ├── config/
│   │   └── SoapClientConfig.java              # Config cliente SOAP
│   ├── controller/
│   │   └── CustomerRestController.java        # REST endpoints
│   └── soap/generated/                        # Clases generadas desde WSDL
│       ├── CustomerService.java
│       ├── CustomerServicePortType.java
│       ├── Customer.java
│       └── ...
└── src/main/resources/
    └── application.properties                 # Configuración Spring Boot
```

## 🧪 Testing

Script de pruebas incluido:

```bash
chmod +x test-rest-client.sh
./test-rest-client.sh
```

## ⚠️ Notas Importantes

- **Dos proyectos separados**: SOAP (puerto 8080) y REST (puerto 9090)
- **Dependencia**: El REST API requiere que el SOAP esté corriendo
- **Generación de clases**: Ejecutar `mvn clean compile` después de cambios en el WSDL
- **URL del WSDL**: Configurada en `pom.xml` (plugin cxf-codegen)

## 🔧 Configuración

Editar `application.properties` para cambiar:
- Puerto del servidor REST
- Niveles de logging
- Configuración de Jackson

Para cambiar la URL del servicio SOAP, editar `SoapClientConfig.java`

## 📊 Comparación

| Aspecto | Proyecto SOAP | Proyecto REST |
|---------|---------------|---------------|
| Puerto | 8080 | 9090 |
| Protocolo | SOAP/XML | HTTP/JSON |
| Framework | Apache CXF + Spring | Spring Boot |
| Rol | Servidor | Cliente → Servidor |
| Path | `/soap-ws/services/customer` | `/api/v1/customers` |

## ✨ Ventajas de Proyectos Separados

1. **Desacoplamiento**: REST y SOAP pueden desplegarse independientemente
2. **Escalabilidad**: Escalar REST sin afectar SOAP
3. **Tecnologías**: Usar el stack más apropiado para cada servicio
4. **Mantenimiento**: Equipos diferentes pueden trabajar en cada proyecto
5. **Despliegue**: Estrategias de deploy independientes
