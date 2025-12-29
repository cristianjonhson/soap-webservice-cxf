# Guía de Uso para Postman

## Opción 1: Importar Colección (Recomendado)

### Pasos:
1. **Inicia el servicio**:
   ```bash
   mvn jetty:run
   ```

2. **Abre Postman**

3. **Importar colección**:
   - Click en **Import** (esquina superior izquierda)
   - Arrastra el archivo `SOAP-CustomerService.postman_collection.json`
   - O click en **Upload Files** y selecciónalo
   - Click en **Import**

4. **Usar las peticiones**:
   - Verás la colección "SOAP Customer Service - CXF" en el sidebar
   - Expándela y selecciona cualquier petición
   - Click en **Send**

---

## Opción 2: Crear Peticiones Manualmente

### Configuración Base

**Para TODAS las peticiones SOAP:**
- **Método**: `POST`
- **URL**: `http://localhost:8080/soap-ws/services/customer`
- **Headers**:
  - `Content-Type`: `text/xml; charset=utf-8`

---

### 1. Get All Customers

#### Headers
```
Content-Type: text/xml; charset=utf-8
```

#### Body (raw → XML)
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Respuesta Esperada
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:getAllCustomersResponse xmlns:ns2="http://soap.example.com/">
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
            <phone>+34 600 789 012</phone>
         </customers>
      </ns2:getAllCustomersResponse>
   </soap:Body>
</soap:Envelope>
```

---

### 2. Get Customer by ID

#### Headers
```
Content-Type: text/xml; charset=utf-8
```

#### Body (raw → XML)
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

💡 **Tip**: Cambia el valor de `<id>` para buscar diferentes clientes (1, 2, 3, etc.)

#### Respuesta Esperada
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:getCustomerResponse xmlns:ns2="http://soap.example.com/">
         <customer>
            <id>1</id>
            <name>Juan Pérez</name>
            <email>juan.perez@example.com</email>
            <phone>+34 600 123 456</phone>
         </customer>
      </ns2:getCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

---

### 3. Create Customer

#### Headers
```
Content-Type: text/xml; charset=utf-8
```

#### Body (raw → XML)
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:createCustomerRequest>
         <name>Laura Fernández</name>
         <email>laura.fernandez@example.com</email>
         <phone>+34 622 333 444</phone>
      </soap:createCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

💡 **Nota**: El campo `<phone>` es opcional y puede omitirse

#### Respuesta Esperada
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:createCustomerResponse xmlns:ns2="http://soap.example.com/">
         <customer>
            <id>3</id>
            <name>Laura Fernández</name>
            <email>laura.fernandez@example.com</email>
            <phone>+34 622 333 444</phone>
         </customer>
      </ns2:createCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

---

## Funciones Avanzadas de Postman

### 1. Tests Automáticos

Agrega en la pestaña **Tests** para validar respuestas:

```javascript
// Verificar status 200
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Verificar que es XML
pm.test("Response is XML", function () {
    pm.response.to.have.header("Content-Type");
    pm.expect(pm.response.headers.get("Content-Type")).to.include("xml");
});

// Verificar que contiene datos
pm.test("Response contains customer data", function () {
    pm.expect(pm.response.text()).to.include("customer");
});

// Extraer ID del cliente creado
if (pm.response.text().includes("createCustomerResponse")) {
    const responseXml = new DOMParser().parseFromString(pm.response.text(), "text/xml");
    const customerId = responseXml.querySelector("id").textContent;
    pm.environment.set("lastCustomerId", customerId);
    console.log("Customer ID created:", customerId);
}
```

### 2. Variables de Entorno

Crea un entorno con estas variables:

```json
{
  "baseUrl": "http://localhost:8080/soap-ws/services",
  "customerId": "1"
}
```

Úsalas en las peticiones:
```
{{baseUrl}}/customer
```

### 3. Pre-request Scripts

Para generar datos dinámicos:

```javascript
// Generar email aleatorio
const randomEmail = `user${Math.floor(Math.random() * 1000)}@example.com`;
pm.environment.set("randomEmail", randomEmail);

// Generar nombre aleatorio
const names = ["Carlos", "Ana", "Luis", "María", "Pedro"];
const randomName = names[Math.floor(Math.random() * names.length)];
pm.environment.set("randomName", randomName);
```

Luego en el Body:
```xml
<name>{{randomName}}</name>
<email>{{randomEmail}}</email>
```

---

## Visualizar WSDL en Postman

### Método 1: GET Request
- **Método**: `GET`
- **URL**: `http://localhost:8080/soap-ws/services/customer?wsdl`
- Click en **Send**
- Verás el WSDL completo en la respuesta

### Método 2: Navegador
Abre directamente en tu navegador:
```
http://localhost:8080/soap-ws/services/customer?wsdl
```

---

## Troubleshooting

### ❌ Error: "Could not send request"
**Causa**: El servicio no está ejecutándose  
**Solución**: Ejecuta `mvn jetty:run` en la terminal

### ❌ Error: "SOAP Fault - No binding operation"
**Causa**: Petición SOAP mal formada  
**Solución**: 
- Verifica que el XML esté correcto
- Asegúrate de usar el namespace correcto: `xmlns:soap="http://soap.example.com/"`
- Verifica que el método sea POST, no GET

### ❌ Error: "Connection refused"
**Causa**: Puerto 8080 ocupado o servicio no iniciado  
**Solución**:
```bash
# Verificar si algo usa el puerto 8080
lsof -i :8080

# Iniciar el servicio
mvn jetty:run
```

### ❌ Respuesta vacía o incorrecta
**Solución**: 
- Verifica los logs del servidor en la terminal
- Comprueba que el Content-Type sea `text/xml; charset=utf-8`
- Valida el XML con un validador online

---

## Exportar y Compartir

### Exportar Colección
1. Click derecho en la colección
2. **Export**
3. Selecciona **Collection v2.1**
4. Guarda el archivo JSON

### Exportar Entorno
1. Click en el ⚙️ (Settings) junto a los entornos
2. Selecciona tu entorno
3. Click en los tres puntos **...** → **Export**
4. Guarda el archivo JSON

---

## Recursos

- **Postman Learning Center**: https://learning.postman.com/
- **SOAP API Testing**: https://learning.postman.com/docs/sending-requests/soap/
- **Variables en Postman**: https://learning.postman.com/docs/sending-requests/variables/

---

¡Ahora estás listo para probar el servicio SOAP en Postman! 🚀
