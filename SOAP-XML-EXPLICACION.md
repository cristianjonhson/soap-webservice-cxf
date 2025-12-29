# 📨 SOAP: Siempre XML de Entrada y Salida

## ✅ Respuesta Corta

**Sí, en SOAP siempre se envía y recibe XML.** Ese es el fundamento del protocolo.

---

## 📤📥 Flujo de Datos Completo

### Cliente → Servidor (Request)
```xml
<!-- Siempre XML con estructura SOAP -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getCustomerRequest>
         <id>1</id>
      </getCustomerRequest>
   </soap:Body>
</soap:Envelope>
```

### Servidor → Cliente (Response)
```xml
<!-- Siempre XML con estructura SOAP -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
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
```

---

## 🔑 Características Clave de SOAP

### 1. XML Obligatorio
- **SOAP = Simple Object Access Protocol**
- **Basado 100% en XML** - No puede ser JSON, texto plano, o cualquier otro formato
- El XML debe seguir la especificación SOAP

### 2. Estructura Fija
Todos los mensajes SOAP tienen esta estructura obligatoria:

```xml
<soap:Envelope>          <!-- Raíz obligatoria -->
   <soap:Header>         <!-- Opcional: metadata, autenticación, etc. -->
      <!-- Headers opcionales -->
   </soap:Header>
   
   <soap:Body>           <!-- Obligatorio: contenido del mensaje -->
      <!-- Tu operación y datos aquí -->
   </soap:Body>
   
   <soap:Fault>          <!-- Opcional: solo en caso de error -->
      <!-- Información del error -->
   </soap:Fault>
</soap:Envelope>
```

### 3. HTTP como Transporte
- El XML viaja dentro de un **HTTP POST**
- Header obligatorio: `Content-Type: text/xml`
- URL del endpoint: `http://localhost:8080/soap-ws/services/customer`

---

## 🔄 Ejemplos Reales del Proyecto

### Ejemplo 1: Obtener Todos los Clientes

**Request (Cliente envía):**
```xml
POST http://localhost:8080/soap-ws/services/customer
Content-Type: text/xml

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getAllCustomersRequest/>
   </soap:Body>
</soap:Envelope>
```

**Response (Servidor responde):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getAllCustomersResponse>
         <customers>
            <id>1</id>
            <name>Juan Pérez</name>
            <email>juan.perez@example.com</email>
            <phone>+34 600 123 456</phone>
         </customers>
         <customers>
            <id>2</id>
            <name>María García</name>
            <email>maria.garcia@example.com</email>
            <phone>+34 600 654 321</phone>
         </customers>
      </getAllCustomersResponse>
   </soap:Body>
</soap:Envelope>
```

### Ejemplo 2: Crear un Cliente

**Request (Cliente envía):**
```xml
POST http://localhost:8080/soap-ws/services/customer
Content-Type: text/xml

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <createCustomerRequest>
         <name>Pedro López</name>
         <email>pedro.lopez@example.com</email>
         <phone>+34 600 111 222</phone>
      </createCustomerRequest>
   </soap:Body>
</soap:Envelope>
```

**Response (Servidor responde):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <createCustomerResponse>
         <customer>
            <id>3</id>
            <name>Pedro López</name>
            <email>pedro.lopez@example.com</email>
            <phone>+34 600 111 222</phone>
         </customer>
      </createCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

### Ejemplo 3: Error (SOAP Fault)

**Request (Cliente envía ID inexistente):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getCustomerRequest>
         <id>999</id>  <!-- Cliente que no existe -->
      </getCustomerRequest>
   </soap:Body>
</soap:Envelope>
```

**Response (Servidor responde con Fault):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <soap:Fault>
         <faultcode>soap:Server</faultcode>
         <faultstring>Cliente no encontrado con ID: 999</faultstring>
      </soap:Fault>
   </soap:Body>
</soap:Envelope>
```

---

## 🆚 Comparación: SOAP vs REST

| Aspecto | SOAP | REST |
|---------|------|------|
| **Formato** | XML (obligatorio) | JSON, XML, texto, etc. (flexible) |
| **Estructura** | Fija: `<soap:Envelope>` | Libre: depende del desarrollador |
| **Protocolo** | Solo HTTP/HTTPS | HTTP/HTTPS |
| **Headers** | `Content-Type: text/xml` | `Content-Type: application/json` |
| **Verbosity** | Muy verboso (mucho XML) | Ligero (JSON compacto) |
| **Contrato** | WSDL formal | Documentación informal |
| **Validación** | Estricta (XSD Schema) | Opcional (JSON Schema) |

### Ejemplo Comparativo

**SOAP XML:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getCustomerResponse>
         <customer>
            <id>1</id>
            <name>Juan Pérez</name>
            <email>juan.perez@example.com</email>
         </customer>
      </getCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

**REST JSON (mismo contenido):**
```json
{
  "customer": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan.perez@example.com"
  }
}
```

---

## 💡 Lo Que Hace Apache CXF Por Ti

Aunque SOAP siempre usa XML, **tú no trabajas directamente con XML** en tu código Java:

```
┌─────────────────────────────────────────────────────────────┐
│                    PERSPECTIVA DEL CLIENTE                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1. Envía XML
                              ▼
                    ┌──────────────────┐
                    │  <soap:Envelope> │
                    │    <soap:Body>   │
                    │      <id>1</id>  │
                    │    </soap:Body>  │
                    │  </soap:Envelope>│
                    └──────────────────┘
                              │
                              │ 2. CXF intercepta
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      APACHE CXF + JAXB                       │
│                                                              │
│  XML  →  [JAXB Unmarshalling]  →  Objetos Java              │
│                                                              │
│  GetCustomerRequest request = new GetCustomerRequest();     │
│  request.setId(1L);                                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 3. Pasa a tu código
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    TU CÓDIGO JAVA                            │
│                                                              │
│  @Override                                                   │
│  public GetCustomerResponse getCustomer(                     │
│      GetCustomerRequest request) {                           │
│                                                              │
│      Long id = request.getId();  // Trabajas con Java       │
│      Customer customer = database.get(id);                   │
│                                                              │
│      GetCustomerResponse response = new GetCustomerResponse();│
│      response.setCustomer(customer);                         │
│      return response;  // Devuelves objetos Java            │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 4. CXF convierte
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      APACHE CXF + JAXB                       │
│                                                              │
│  Objetos Java  →  [JAXB Marshalling]  →  XML                │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 5. Envía XML
                              ▼
                    ┌──────────────────────┐
                    │  <soap:Envelope>     │
                    │    <soap:Body>       │
                    │      <customer>      │
                    │        <id>1</id>    │
                    │        <name>...</>  │
                    │      </customer>     │
                    │    </soap:Body>      │
                    │  </soap:Envelope>    │
                    └──────────────────────┘
                              │
                              │ 6. Cliente recibe XML
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    PERSPECTIVA DEL CLIENTE                   │
└─────────────────────────────────────────────────────────────┘
```

### Resumen de Conversiones

```
Cliente:     XML → HTTP POST
    ↓
CXF/JAXB:    XML → Objetos Java (Unmarshalling)
    ↓
Tu Código:   Trabajas con Customer, GetCustomerRequest, etc.
    ↓
CXF/JAXB:    Objetos Java → XML (Marshalling)
    ↓
Cliente:     HTTP Response ← XML
```

---

## 🎯 Ventajas de Usar Siempre XML

### ✅ Ventajas:

1. **Validación Estricta**
   - El XML se valida contra el esquema XSD definido en el WSDL
   - Errores detectados antes de procesar

2. **Contratos Formales**
   - El WSDL define exactamente qué XML es válido
   - Cliente y servidor están de acuerdo desde el inicio

3. **Interoperabilidad**
   - XML es un estándar universal
   - Cualquier lenguaje puede leer/escribir XML SOAP

4. **Metadatos Ricos**
   - `<soap:Header>` para autenticación, transacciones, etc.
   - Estándares WS-* (WS-Security, WS-ReliableMessaging)

5. **Trazabilidad**
   - Fácil de loggear y auditar
   - Formato legible por humanos

### ❌ Desventajas:

1. **Verbosidad**
   - Mucho texto por cada mensaje
   - Mayor uso de ancho de banda

2. **Complejidad**
   - Curva de aprendizaje más alta que REST/JSON
   - Configuración más compleja

3. **Performance**
   - Parsing XML más lento que JSON
   - Mayor overhead de red

---

## 📝 Headers HTTP en SOAP

Cada petición SOAP incluye estos headers HTTP:

```http
POST /soap-ws/services/customer HTTP/1.1
Host: localhost:8080
Content-Type: text/xml; charset=utf-8
Content-Length: 423
SOAPAction: ""

<soap:Envelope>
   ...
</soap:Envelope>
```

**Headers importantes:**
- `Content-Type: text/xml` - Indica que el body es XML
- `SOAPAction: ""` - Identifica la operación SOAP (opcional en este proyecto)
- `charset=utf-8` - Codificación de caracteres

---

## 🛠️ Herramientas para Ver el XML

### 1. cURL (Command Line)
```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope>...</soap:Envelope>' \
  -v  # Modo verbose para ver headers y XML
```

### 2. Postman
- Pestaña **Body** → **raw** → seleccionar **XML**
- Ver XML de request en el editor
- Ver XML de response en la sección inferior

### 3. SoapUI
- Muestra el XML de request en panel izquierdo
- Muestra el XML de response en panel derecho
- Resalta errores de validación XML

### 4. Browser DevTools
- **Network tab** → Seleccionar la petición SOAP
- **Payload** - Ver el XML enviado
- **Response** - Ver el XML recibido

---

## 🧪 Prueba Real con Este Proyecto

Ejecuta el script de pruebas y observa el XML:

```bash
./test-soap.sh
```

**Salida esperada:**
```
===========================================
Test 1: Verificar WSDL
===========================================
<?xml version='1.0' encoding='UTF-8'?>
<wsdl:definitions ...>
...

===========================================
Test 2: Obtener todos los clientes
===========================================
<soap:Envelope ...>
   <soap:Body>
      <getAllCustomersResponse>
         <customers>
            <id>1</id>
            <name>Juan Pérez</name>
            ...
```

**Cada línea es XML.** No hay JSON, no hay texto plano, solo XML.

---

## 🎓 Conclusión

### En SOAP:
- ✅ **Siempre XML en entrada**
- ✅ **Siempre XML en salida**
- ✅ **Estructura `<soap:Envelope>` obligatoria**
- ✅ **CXF traduce XML ↔ Java automáticamente**
- ✅ **Tú trabajas con objetos Java, no con XML directo**

### El XML viaja así:
```
[Cliente] → XML → [HTTP] → [Servidor CXF] → Java Objects → [Tu Código]
                                                                ↓
[Cliente] ← XML ← [HTTP] ← [Servidor CXF] ← Java Objects ← [Tu Código]
```

**No hay excepciones.** Si no es XML, no es SOAP.

---

## 📚 Referencias

- [W3C SOAP Specification](https://www.w3.org/TR/soap/)
- [COMO-FUNCIONA.md](COMO-FUNCIONA.md) - Explicación completa del proyecto
- [README.md](README.md) - Guía de inicio rápido
- [test-soap.sh](test-soap.sh) - Script con ejemplos XML reales
