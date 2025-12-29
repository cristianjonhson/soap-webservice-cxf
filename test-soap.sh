#!/bin/bash

echo "==================================="
echo "Probando Web Service SOAP con cURL"
echo "==================================="
echo ""

# Verificar que el servidor esté corriendo
echo "1. Verificando WSDL..."
curl -s "http://localhost:8080/soap-ws/services/customer?wsdl" | head -5
echo ""

echo "2. Obteniendo todos los clientes..."
curl -s -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: http://soap.example.com/getAllCustomers" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getAllCustomersRequest/>
   </soapenv:Body>
</soapenv:Envelope>'
echo ""
echo ""

echo "3. Obteniendo cliente con ID 1..."
curl -s -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: http://soap.example.com/getCustomer" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getCustomerRequest>
         <id>1</id>
      </soap:getCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>'
echo ""
echo ""

echo "4. Creando nuevo cliente..."
curl -s -X POST http://localhost:8080/soap-ws/services/customer \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: http://soap.example.com/createCustomer" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:createCustomerRequest>
         <name>Pedro López</name>
         <email>pedro.lopez@example.com</email>
         <phone>+34 600 999 888</phone>
      </soap:createCustomerRequest>
   </soapenv:Body>
</soapenv:Envelope>'
echo ""
