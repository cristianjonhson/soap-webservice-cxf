# SOAP Web Service - Estructura del Repositorio

Este repositorio contiene **DOS PROYECTOS INDEPENDIENTES** que trabajan juntos:

## 📁 Estructura

```
soap-webservice-cxf/
├── src/                          # 🟦 PROYECTO SOAP (Servidor)
│   └── main/
│       ├── java/
│       │   └── com/example/soap/
│       │       ├── CustomerService.java
│       │       ├── CustomerServiceImpl.java
│       │       └── model/Customer.java
│       └── webapp/
│           └── WEB-INF/
│               ├── web.xml
│               └── customer.xsd
├── pom.xml                       # 🟦 Maven config SOAP
├── test-soap.sh                  # 🟦 Tests SOAP
│
└── rest-client-soap/             # 🟩 PROYECTO REST (Cliente)
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/
    │   │   │   ├── RestClientSoapApplication.java
    │   │   │   ├── config/SoapClientConfig.java
    │   │   │   └── controller/CustomerRestController.java
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    ├── pom.xml                   # 🟩 Maven config REST
    ├── test-rest-client.sh       # 🟩 Tests REST
    └── README.md                 # 🟩 Documentación REST
```

## 🎯 Proyectos

### 1️⃣ Servidor SOAP (Raíz del repositorio)

**Ubicación**: `/` (raíz)  
**Puerto**: 8080  
**Tecnología**: Apache CXF + Jetty  
**Función**: Servicio web SOAP tradicional

#### Iniciar:
```bash
mvn jetty:run
```

#### Verificar:
```bash
curl http://localhost:8080/soap-ws/services/customer?wsdl
./test-soap.sh
```

### 2️⃣ Cliente REST (Subcarpeta)

**Ubicación**: `/rest-client-soap/`  
**Puerto**: 9090  
**Tecnología**: Spring Boot + CXF Client  
**Función**: Gateway REST que consume el servicio SOAP

#### Iniciar:
```bash
cd rest-client-soap
mvn spring-boot:run
```

#### Verificar:
```bash
curl http://localhost:9090/api/v1/customers/health
cd rest-client-soap && ./test-rest-client.sh
```

## 🔄 Arquitectura Completa

```
┌─────────────────┐
│  Cliente HTTP   │
└────────┬────────┘
         │ HTTP/JSON
         ▼
┌─────────────────────────┐
│  REST API (9090)        │  ← 🟩 rest-client-soap/
│  Spring Boot            │
└────────┬────────────────┘
         │ SOAP/XML (CXF Client)
         ▼
┌─────────────────────────┐
│  SOAP Service (8080)    │  ← 🟦 raíz del repo
│  Apache CXF + Jetty     │
└─────────────────────────┘
```

## 🚀 Inicio Rápido

### Opción 1: Ejecutar ambos servicios

#### Terminal 1 - SOAP Server:
```bash
mvn jetty:run
```

#### Terminal 2 - REST Client:
```bash
cd rest-client-soap
mvn spring-boot:run
```

### Opción 2: Solo SOAP

```bash
mvn jetty:run
./test-soap.sh
```

## 📊 Comparación

| Aspecto | SOAP (Puerto 8080) | REST (Puerto 9090) |
|---------|-------------------|-------------------|
| **Protocolo** | SOAP/XML | REST/JSON |
| **Framework** | Apache CXF | Spring Boot |
| **Servidor** | Jetty | Tomcat (embebido) |
| **Función** | Servicio original | Gateway/Cliente |
| **Dependencias** | Ninguna | Requiere SOAP corriendo |
| **Complejidad** | XML verboso | JSON simple |
| **Estándar** | WS-* specs | HTTP estándar |

## 🧪 Testing

### Test SOAP:
```bash
./test-soap.sh
```

### Test REST:
```bash
cd rest-client-soap
./test-rest-client.sh
```

### Verificar ambos servicios:
```bash
# SOAP
curl http://localhost:8080/soap-ws/services/customer?wsdl

# REST
curl http://localhost:9090/api/v1/customers/health
```

## 📦 Builds Independientes

Cada proyecto tiene su propio ciclo de build Maven:

### Build SOAP:
```bash
mvn clean install
```

### Build REST:
```bash
cd rest-client-soap
mvn clean package
```

## 🔗 URLs Importantes

### SOAP Service:
- **WSDL**: http://localhost:8080/soap-ws/services/customer?wsdl
- **Endpoint**: http://localhost:8080/soap-ws/services/customer

### REST API:
- **Base**: http://localhost:9090/api/v1/customers
- **Health**: http://localhost:9090/api/v1/customers/health
- **Docs**: [rest-client-soap/README.md](rest-client-soap/README.md)

## 📚 Documentación Adicional

- **SOAP Service**: Ver [README.md](README.md) en la raíz
- **REST Client**: Ver [rest-client-soap/README.md](rest-client-soap/README.md)
- **Arquitectura REST**: [REST-SOAP-INTEGRACION.md](REST-SOAP-INTEGRACION.md)
- **Cómo funciona SOAP**: [COMO-FUNCIONA.md](COMO-FUNCIONA.md)
- **XML vs XSD**: [XSD-vs-XML.md](XSD-vs-XML.md)

## 💡 Casos de Uso

### Solo necesitas SOAP:
```bash
mvn jetty:run
```

### Quieres API REST moderna:
```bash
# Terminal 1
mvn jetty:run

# Terminal 2
cd rest-client-soap && mvn spring-boot:run
```

### Desarrollo frontend:
- Usa el REST API (puerto 9090) para integración moderna
- El SOAP queda encapsulado y no necesitas lidiar con XML

## 🛠️ Desarrollo

### Modificar SOAP:
1. Editar archivos en `src/main/java/com/example/soap/`
2. Reiniciar: `mvn jetty:run`

### Modificar REST:
1. `cd rest-client-soap`
2. Editar archivos en `src/main/java/com/example/`
3. Reiniciar: `mvn spring-boot:run`

## 🔐 Notas Importantes

- **Dependencia**: El REST client requiere que el SOAP server esté corriendo
- **Build Time**: El REST client necesita el SOAP WSDL disponible durante el build
- **Puertos**: SOAP=8080, REST=9090 (configurables)
- **Independencia**: Ambos son proyectos Maven independientes
- **Repositorio**: Un solo repo git, dos proyectos Maven
