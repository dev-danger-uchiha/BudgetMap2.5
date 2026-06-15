#!/bin/bash

# BudgetMap - Script de inicio

echo "🚀 Iniciando BudgetMap..."

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado"
    exit 1
fi

# Navegar al directorio docker
cd "$(dirname "$0")/../docker" || exit

# Iniciar servicios
echo "📦 Construyendo e iniciando contenedores..."
docker-compose up --build -d

# Esperar a que MySQL esté listo
echo "⏳ Esperando a que MySQL esté listo..."
sleep 30

# Verificar servicios
echo "🔍 Verificando servicios..."

if curl -s https://budgetmap-api.onrender.com/health > /dev/null; then
    echo "✅ Spring Boot API está corriendo en https://budgetmap-api.onrender.com"
else
    echo "⚠️  Spring Boot API no responde todavía"
fi

if curl -s http://localhost:5000/health > /dev/null; then
    echo "✅ Flask Geo Service está corriendo en http://localhost:5000"
else
    echo "⚠️  Flask Geo Service no responde todavía"
fi

echo ""
echo "🎉 BudgetMap está iniciando!"
echo ""
echo "📋 Servicios disponibles:"
echo "   - Spring Boot API: https://budgetmap-api.onrender.com"
echo "   - Flask Geo:       http://localhost:5000"
echo "   - phpMyAdmin:      http://localhost:8081"
echo ""
echo "📖 Credenciales de prueba:"
echo "   - admin@budgetmap.com / admin123"
echo "   - moderador@budgetmap.com / mod123"
echo "   - local@budgetmap.com / local123"
echo "   - anfitrion@budgetmap.com / anfi123"
echo "   - explorador@budgetmap.com / user123"
echo ""
echo "🛑 Para detener: docker-compose down"
