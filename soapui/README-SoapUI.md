# Guía de Importación para SoapUI

## Opción 1: Importar desde WSDL (Recomendado)

### Pasos:
1. **Inicia el servicio**:
   ```bash
   mvn jetty:run
   ```

2. **Abre SoapUI** (o SoapUI Open Source)

3. **Crear nuevo proyecto**:
   - File → New SOAP Project
   - **Project Name**: `CustomerService`
   - **Initial WSDL**: `http://localhost:8080/soap-ws/services/customer?wsdl`
   - ✅ **Create Requests**: activado
   - Click en **OK**

4. **Explorar las operaciones**:
   SoapUI generará automáticamente:
   - `getAllCustomers`
   - `getCustomer`
   - `createCustomer`

5. **Ejecutar peticiones**:
   - Expande `CustomerServiceBinding → operación deseada`
   - Doble click en `Request 1`
   - Modifica los valores según necesites
   - Click en el botón ▶️ (play) verde

---

## Opción 2: Peticiones Manuales

### getAllCustomers

**Endpoint**: `http://localhost:8080/soap-ws/services/customer`

**Request**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

---

### getCustomer

**Endpoint**: `http://localhost:8080/soap-ws/services/customer`

**Request**:
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

**Ejemplo de Response**:
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

### createCustomer

**Endpoint**: `http://localhost:8080/soap-ws/services/customer`

**Request**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:createCustomerRequest>
         <name>Carlos Rodríguez</name>
         <email>carlos.rodriguez@example.com</email>
         <phone>+34 611 222 333</phone>
      </soap:createCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Ejemplo de Response**:
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:createCustomerResponse xmlns:ns2="http://soap.example.com/">
         <customer>
            <id>3</id>
            <name>Carlos Rodríguez</name>
            <email>carlos.rodriguez@example.com</email>
            <phone>+34 611 222 333</phone>
         </customer>
      </ns2:createCustomerResponse>
   </soap:Body>
</soap:Envelope>
```

---

## Tips para SoapUI

### Validación de Respuestas
1. Click derecho en la petición → **Add Assertion**
2. Selecciona:
   - **SOAP Response** - Valida que sea SOAP válido
   - **Schema Compliance** - Valida contra el WSDL
   - **XPath Match** - Valida contenido específico

### Ejemplo de XPath Assertion
Para validar que el nombre del cliente sea correcto:
```xpath
//ns:name/text()
```
Expected Result: `Juan Pérez`

### Variables y Propiedades
Puedes usar propiedades para reutilizar valores:
```xml
<id>${#Project#customerId}</id>
```

---

## Troubleshooting

### Error: "Connection refused"
✅ **Solución**: Asegúrate que el servicio esté corriendo con `mvn jetty:run`

### Error: "No binding operation info"
✅ **Solución**: Verifica que estés enviando una petición SOAP POST válida, no un GET simple

### Error al cargar WSDL
✅ **Solución**: 
1. Verifica que el servicio esté ejecutándose
2. Intenta acceder a: `http://localhost:8080/soap-ws/services/customer?wsdl` en el navegador
3. Si funciona, copia el contenido y guárdalo como archivo `.wsdl`
4. Importa el archivo local en SoapUI

---

## Recursos Adicionales

- **SoapUI Documentation**: https://www.soapui.org/docs/
- **Apache CXF**: https://cxf.apache.org/
- **SOAP Specification**: https://www.w3.org/TR/soap/
