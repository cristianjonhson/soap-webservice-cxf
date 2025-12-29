#!/bin/bash

# Script de pruebas para REST API Client

BASE_URL="http://localhost:9090/api/v1/customers"

echo "=========================================="
echo "REST API Client - Pruebas"
echo "=========================================="
echo ""

# Test 1: Health Check
echo "=========================================="
echo "Test 1: Health Check"
echo "=========================================="
curl -s "$BASE_URL/health" | jq '.' || curl -s "$BASE_URL/health"
echo ""
echo ""

# Test 2: GET todos los clientes
echo "=========================================="
echo "Test 2: Obtener todos los clientes (GET)"
echo "=========================================="
curl -s "$BASE_URL" | jq '.' || curl -s "$BASE_URL"
echo ""
echo ""

# Test 3: GET cliente por ID
echo "=========================================="
echo "Test 3: Obtener cliente por ID (GET /1)"
echo "=========================================="
curl -s "$BASE_URL/1" | jq '.' || curl -s "$BASE_URL/1"
echo ""
echo ""

# Test 4: GET otro cliente
echo "=========================================="
echo "Test 4: Obtener cliente por ID (GET /2)"
echo "=========================================="
curl -s "$BASE_URL/2" | jq '.' || curl -s "$BASE_URL/2"
echo ""
echo ""

# Test 5: POST crear cliente
echo "=========================================="
echo "Test 5: Crear nuevo cliente (POST)"
echo "=========================================="
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Martínez REST Client",
    "email": "ana.martinez@restclient.com",
    "phone": "+34 600 999 888"
  }' | jq '.' || curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Martínez REST Client",
    "email": "ana.martinez@restclient.com",
    "phone": "+34 600 999 888"
  }'
echo ""
echo ""

# Test 6: Verificar creación
echo "=========================================="
echo "Test 6: Verificar que se creó (GET todos)"
echo "=========================================="
curl -s "$BASE_URL" | jq '.' || curl -s "$BASE_URL"
echo ""
echo ""

# Test 7: Cliente no encontrado
echo "=========================================="
echo "Test 7: Cliente no encontrado (GET /999)"
echo "=========================================="
curl -s "$BASE_URL/999" -w "\nHTTP Status: %{http_code}\n"
echo ""
echo ""

echo "=========================================="
echo "✅ Pruebas completadas"
echo "=========================================="
echo ""
echo "🔗 REST API: http://localhost:9090/api/v1/customers"
echo "🔗 SOAP Service: http://localhost:8080/soap-ws/services/customer"
echo ""
