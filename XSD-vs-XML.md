# 📐 XSD vs XML en SOAP: ¿Qué se Envía?

## 🎯 Respuesta Corta

El **XSD** (XML Schema Definition) **NO se envía** en las peticiones SOAP. 

El XSD está **dentro del WSDL** y define **cómo deben ser** los mensajes XML que se envían/reciben.

---

## 🔑 Conceptos Clave

### XSD (XML Schema Definition)
- Es un **esquema/molde** que define la estructura de los datos
- Define tipos de datos, elementos, atributos, restricciones
- Está **embebido en el WSDL**
- Se descarga **una sola vez** cuando el cliente lee el WSDL

### XML (Extensible Markup Language)
- Son los **mensajes de datos** reales que se envían
- Debe **cumplir** con el esquema XSD
- Se envía en **cada petición y respuesta**

---

## 📋 Cómo Funciona en Este Proyecto

### 1️⃣ El XSD está en el WSDL (Una sola vez)

En tu archivo [`CustomerService.wsdl`](src/main/resources/CustomerService.wsdl) (líneas 7-44):

```xml
<wsdl:definitions>
  <wsdl:types>
    <!-- AQUÍ está el XSD Schema que define la estructura -->
    <xsd:schema targetNamespace="http://soap.example.com/">
      
      <!-- Define CÓMO debe ser un Customer -->
      <xsd:complexType name="Customer">
        <xsd:sequence>
          <xsd:element name="id" type="xsd:long" minOccurs="0"/>
          <xsd:element name="name" type="xsd:string" minOccurs="0"/>
          <xsd:element name="email" type="xsd:string" minOccurs="0"/>
          <xsd:element name="phone" type="xsd:string" minOccurs="0"/>
        </xsd:sequence>
      </xsd:complexType>
      
      <!-- Define la estructura de las peticiones -->
      <xsd:element name="getCustomerRequest">
        <xsd:complexType>
          <xsd:sequence>
            <xsd:element name="id" type="xsd:long"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:element>
      
      <!-- Define la estructura de las respuestas -->
      <xsd:element name="getCustomerResponse">
        <xsd:complexType>
          <xsd:sequence>
            <xsd:element name="customer" type="tns:Customer"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:element>
      
    </xsd:schema>
  </wsdl:types>
</wsdl:definitions>
```

**El XSD dice:**
- "Un Customer tiene estos 4 campos: id (long), name (string), email (string), phone (string)"
- "Una petición getCustomer debe tener un id de tipo long"
- "Una respuesta getCustomer debe tener un objeto Customer"

### 2️⃣ Los mensajes XML siguen el esquema XSD

**Request (XML que cumple el schema):**
```xml
POST http://localhost:8080/soap-ws/services/customer
Content-Type: text/xml

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getCustomerRequest>
         <id>1</id>  <!-- ✅ Cumple: xsd:long según el schema -->
      </getCustomerRequest>
   </soap:Body>
</soap:Envelope>
```

**Response (XML que cumple el schema):**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <getCustomerResponse>
         <customer>  <!-- ✅ Cumple: complexType Customer -->
            <id>1</id>           <!-- xsd:long ✅ -->
            <name>Juan Pérez</name>  <!-- xsd:string ✅ -->
            <email>juan@example.com</email>  <!-- xsd:string ✅ -->
            <phone>+34 600 123 456</phone>  <!-- xsd:string ✅ -->
         </customer>
      </getCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

---

## 🔄 Flujo Completo

```
┌─────────────────────────────────────────────────────────────┐
│  PASO 1: WSDL con XSD (Publicado en el servidor)           │
│  URL: http://localhost:8080/soap-ws/services/customer?wsdl │
│                                                             │
│  <wsdl:definitions>                                         │
│    <wsdl:types>                                             │
│      <xsd:schema>                                           │
│        <xsd:complexType name="Customer">                    │
│          <xsd:element name="id" type="xsd:long"/>           │
│          <xsd:element name="name" type="xsd:string"/>       │
│          <!-- Define la ESTRUCTURA de los datos -->         │
│        </xsd:complexType>                                   │
│      </xsd:schema>                                          │
│    </wsdl:types>                                            │
│  </wsdl:definitions>                                        │
└─────────────────────────────────────────────────────────────┘
                    │
                    │ El cliente descarga el WSDL UNA vez
                    ▼
┌─────────────────────────────────────────────────────────────┐
│  PASO 2: Cliente genera código desde el WSDL               │
│                                                             │
│  - Lee el XSD dentro del WSDL                               │
│  - Genera clases Java: Customer.java, GetCustomerRequest   │
│  - Ahora el cliente CONOCE el esquema                       │
│  - NO necesita el XSD en cada petición                      │
└─────────────────────────────────────────────────────────────┘
                    │
                    │ En CADA petición:
                    ▼
┌─────────────────────────────────────────────────────────────┐
│  PASO 3: Cliente envía XML (NO envía el XSD)               │
│                                                             │
│  <soap:Envelope>                                            │
│    <soap:Body>                                              │
│      <getCustomerRequest>                                   │
│        <id>1</id>  <!-- Solo datos XML -->                  │
│      </getCustomerRequest>                                  │
│    </soap:Body>                                             │
│  </soap:Envelope>                                           │
│                                                             │
│  ⚠️  NO se incluye el XSD aquí                              │
└─────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────┐
│  PASO 4: Servidor valida el XML contra el XSD              │
│                                                             │
│  CXF compara:                                               │
│  ✅ ¿El XML tiene un elemento <id>? → SÍ                    │
│  ✅ ¿El <id> es de tipo long? → SÍ                          │
│  ✅ ¿Cumple todas las reglas del XSD? → SÍ                  │
│                                                             │
│  → Procesa la petición                                      │
│                                                             │
│  ❌ Si NO cumple → Error SOAP Fault                         │
└─────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────┐
│  PASO 5: Servidor responde XML (NO envía el XSD)           │
│                                                             │
│  <soap:Envelope>                                            │
│    <soap:Body>                                              │
│      <getCustomerResponse>                                  │
│        <customer>                                           │
│          <id>1</id>                                         │
│          <name>Juan Pérez</name>                            │
│          <email>juan@example.com</email>                    │
│          <phone>+34 600 123 456</phone>                     │
│        </customer>                                          │
│      </getCustomerResponse>                                 │
│    </soap:Body>                                             │
│  </soap:Envelope>                                           │
│                                                             │
│  ⚠️  NO se incluye el XSD aquí tampoco                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 💡 Analogía: Plano de Casa vs Casa Real

### XSD = Plano/Molde (Diseño)

```
┌─────────────────────────┐
│   PLANO DE CASA (XSD)   │
├─────────────────────────┤
│ - Puerta: 2m altura     │
│ - Ventanas: 1.5m ancho  │
│ - Habitaciones: 3       │
│ - Color: Especificado   │
└─────────────────────────┘
```

- El plano define cómo debe ser la casa
- Se crea **una vez**
- Se consulta cuando necesitas construir
- **NO lo envías cada vez que visitas la casa**

### XML = Casa Construida (Datos)

```
┌─────────────────────────┐
│   CASA REAL (XML)       │
├─────────────────────────┤
│ 🚪 Puerta: 2m ✅        │
│ 🪟 Ventanas: 1.5m ✅    │
│ 🏠 Habitaciones: 3 ✅   │
│ 🎨 Color: Azul ✅       │
└─────────────────────────┘
```

- La casa construida sigue el plano
- Cada casa es una **instancia** del plano
- Puedes construir **muchas casas** del mismo plano
- La casa **debe cumplir** las especificaciones del plano

### En SOAP:

| Elemento | Analogía | SOAP |
|----------|----------|------|
| **Plano** | Define estructura | **XSD** (en WSDL) |
| **Casa** | Instancia concreta | **XML** (mensajes) |
| **Arquitecto** | Define reglas | **Desarrollador** escribe WSDL |
| **Constructor** | Valida construcción | **Apache CXF** valida XML |

---

## 🔍 Ejemplo Real del Proyecto

### XSD en el WSDL - Define la Estructura

```xml
<!-- Ubicación: src/main/resources/CustomerService.wsdl -->
<xsd:complexType name="Customer">
  <xsd:sequence>
    <xsd:element name="id" type="xsd:long" minOccurs="0"/>
    <xsd:element name="name" type="xsd:string" minOccurs="0"/>
    <xsd:element name="email" type="xsd:string" minOccurs="0"/>
    <xsd:element name="phone" type="xsd:string" minOccurs="0"/>
  </xsd:sequence>
</xsd:complexType>
```

**El XSD dice:**
- ✅ Un Customer tiene exactamente 4 campos: id, name, email, phone
- ✅ `id` debe ser un número entero largo (long)
- ✅ `name`, `email`, `phone` deben ser cadenas de texto (string)
- ✅ Todos son opcionales (minOccurs="0")
- ❌ NO puedes agregar un campo `edad` (no está definido)
- ❌ NO puedes poner texto en `id` (debe ser número)

### XML que Envías - Cumple el Schema

```xml
<!-- ✅ VÁLIDO: Cumple todas las reglas del XSD -->
<createCustomerRequest>
   <name>Pedro López</name>      <!-- ✅ String -->
   <email>pedro@example.com</email>  <!-- ✅ String -->
   <phone>+34 600 111 222</phone>    <!-- ✅ String -->
</createCustomerRequest>
```

### XML Inválido - NO Cumple el Schema

```xml
<!-- ❌ INVÁLIDO: Tiene un campo no definido -->
<createCustomerRequest>
   <name>Pedro López</name>
   <email>pedro@example.com</email>
   <edad>30</edad>  <!-- ❌ ERROR: 'edad' no existe en el XSD schema -->
</createCustomerRequest>
```

**Resultado:** CXF rechazaría esto con un **SOAP Fault**.

```xml
<!-- ❌ INVÁLIDO: Tipo de dato incorrecto -->
<getCustomerRequest>
   <id>abc123</id>  <!-- ❌ ERROR: 'abc123' no es un long -->
</getCustomerRequest>
```

**Resultado:** CXF rechazaría esto con un error de validación.

---

## 📊 Tabla Comparativa

| Aspecto | XSD Schema | XML Mensaje |
|---------|-----------|-------------|
| **¿Qué es?** | Esquema/Definición | Datos/Instancia |
| **¿Dónde está?** | Dentro del WSDL | En request/response |
| **¿Se envía?** | ❌ NO (se consulta una vez) | ✅ SÍ (en cada petición) |
| **Propósito** | Define la ESTRUCTURA | Contiene los DATOS |
| **Analogía** | Manual de instrucciones | Mensaje siguiendo instrucciones |
| **Formato** | Definición de tipos | Valores concretos |
| **Frecuencia** | Una vez (descarga WSDL) | Cada petición/respuesta |
| **Tamaño** | ~100-500 líneas | ~10-50 líneas |
| **Modificación** | Solo por el desarrollador | Por cada cliente |

---

## 🎯 Validación Automática de CXF

Apache CXF valida automáticamente que el XML cumpla el XSD:

### Validación en Request

```java
// CXF intercepta el XML
String xmlRequest = "<getCustomerRequest><id>abc</id></getCustomerRequest>";

// CXF valida contra el XSD
if (!validaContraXSD(xmlRequest)) {
    // ❌ Lanza SOAP Fault
    throw new SOAPException("Invalid XML: 'id' debe ser long, no string");
}

// ✅ Si pasa la validación, continúa
procesarPeticion(xmlRequest);
```

### Validación en Response

```java
// Tu código crea el objeto
Customer customer = new Customer();
customer.setId(1L);
customer.setName("Juan Pérez");

// CXF valida que cumpla el XSD
if (!validaContraXSD(customer)) {
    // ❌ Error interno
    throw new Exception("Response no cumple XSD");
}

// ✅ CXF convierte a XML
String xmlResponse = convertirAXML(customer);
enviarRespuesta(xmlResponse);
```

---

## 🔧 Cómo Ver el XSD en el WSDL

### 1. Acceder al WSDL

```bash
# Con el servidor corriendo (mvn jetty:run)
curl http://localhost:8080/soap-ws/services/customer?wsdl
```

### 2. Buscar la sección <wsdl:types>

```xml
<?xml version='1.0' encoding='UTF-8'?>
<wsdl:definitions>
  
  <!-- AQUÍ está el XSD completo -->
  <wsdl:types>
    <xsd:schema targetNamespace="http://soap.example.com/">
      <!-- Definiciones de tipos -->
      <xsd:complexType name="Customer">...</xsd:complexType>
      <xsd:element name="getCustomerRequest">...</xsd:element>
      <!-- ... más definiciones ... -->
    </xsd:schema>
  </wsdl:types>
  
  <!-- Resto del WSDL -->
  <wsdl:message>...</wsdl:message>
  <wsdl:portType>...</wsdl:portType>
  <wsdl:binding>...</wsdl:binding>
  <wsdl:service>...</wsdl:service>
  
</wsdl:definitions>
```

### 3. Ver los tipos definidos

En [`CustomerService.wsdl`](src/main/resources/CustomerService.wsdl), la sección `<wsdl:types>` contiene el XSD completo con todas las definiciones.

---

## 🧪 Prueba Práctica

### Experimento 1: Enviar XML válido

```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
        <soap:Body>
          <getCustomerRequest>
            <id>1</id>
          </getCustomerRequest>
        </soap:Body>
      </soap:Envelope>'
```

**Resultado:** ✅ Funciona - Devuelve datos del cliente

### Experimento 2: Enviar XML inválido (campo extra)

```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
        <soap:Body>
          <getCustomerRequest>
            <id>1</id>
            <campoInventado>valor</campoInventado>
          </getCustomerRequest>
        </soap:Body>
      </soap:Envelope>'
```

**Resultado:** ❌ Error - CXF rechaza el XML (campo no definido en XSD)

### Experimento 3: Enviar XML inválido (tipo incorrecto)

```bash
curl -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
        <soap:Body>
          <getCustomerRequest>
            <id>texto_no_numero</id>
          </getCustomerRequest>
        </soap:Body>
      </soap:Envelope>'
```

**Resultado:** ❌ Error - CXF rechaza (esperaba long, recibió string)

---

## 📝 Reglas del XSD en Este Proyecto

### Tipos de Datos Permitidos

```xml
<!-- XSD define estos tipos -->
<xsd:element name="id" type="xsd:long"/>      <!-- Números: 1, 2, 999 -->
<xsd:element name="name" type="xsd:string"/>  <!-- Texto: "Juan", "María" -->
<xsd:element name="email" type="xsd:string"/> <!-- Texto: "juan@example.com" -->
<xsd:element name="phone" type="xsd:string"/> <!-- Texto: "+34 600 123 456" -->
```

### Elementos Requeridos vs Opcionales

```xml
<!-- minOccurs="0" = OPCIONAL -->
<xsd:element name="email" type="xsd:string" minOccurs="0"/>

<!-- Sin minOccurs = REQUERIDO (por defecto minOccurs="1") -->
<xsd:element name="id" type="xsd:long"/>
```

### Orden de Elementos

```xml
<!-- <xsd:sequence> = Orden ESTRICTO -->
<xsd:complexType name="Customer">
  <xsd:sequence>
    <xsd:element name="id" type="xsd:long"/>      <!-- 1º -->
    <xsd:element name="name" type="xsd:string"/>  <!-- 2º -->
    <xsd:element name="email" type="xsd:string"/> <!-- 3º -->
    <xsd:element name="phone" type="xsd:string"/> <!-- 4º -->
  </xsd:sequence>
</xsd:complexType>
```

**Resultado:** El XML debe seguir este orden exacto.

---

## 🎓 Resumen Ejecutivo

### Lo Que SE Envía en Cada Petición

```
Cliente → Servidor
  ✅ XML con datos (SOAP Envelope + Body)
  ❌ NO se envía el XSD
```

### Lo Que NO SE Envía

```
Cliente → Servidor
  ❌ XSD Schema (ya está en el WSDL)
  ❌ WSDL completo (solo se descarga una vez)
```

### Tabla Final

| Elemento | ¿Se envía? | ¿Cuándo? | Ubicación |
|----------|-----------|----------|-----------|
| **WSDL** | ❌ NO | Se consulta una vez | `?wsdl` |
| **XSD** | ❌ NO | Está dentro del WSDL | `<wsdl:types>` |
| **XML Request** | ✅ SÍ | Cada petición | HTTP POST Body |
| **XML Response** | ✅ SÍ | Cada respuesta | HTTP Response Body |

---

## 🚀 Conclusión

**Respuesta corta:**
- ❌ **NO se envía XSD** en las peticiones
- ✅ **SÍ se envía XML** que cumple el XSD
- 📋 El XSD está en el WSDL como **referencia/contrato**
- 📤 El XML son los **datos reales** que viajan

**Analogía final:**
```
XSD = Reglas del juego (manual)
XML = Partida del juego (jugando)
```

No juegas enviando el manual cada vez, solo juegas siguiendo las reglas.

---

## 📚 Referencias

- [COMO-FUNCIONA.md](COMO-FUNCIONA.md) - Arquitectura completa del proyecto
- [SOAP-XML-EXPLICACION.md](SOAP-XML-EXPLICACION.md) - Detalle sobre XML en SOAP
- [CustomerService.wsdl](src/main/resources/CustomerService.wsdl) - WSDL con XSD incluido
- [W3C XML Schema](https://www.w3.org/TR/xmlschema-0/) - Especificación oficial XSD
