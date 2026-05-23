#!/bin/bash

# BudgetMap - Script de logs

cd "$(dirname "$0")/../docker" || exit

if [ -z "$1" ]; then
    echo "📋 Uso: ./logs.sh [servicio]"
    echo ""
    echo "Servicios disponibles:"
    echo "   - api      (Spring Boot)"
    echo "   - geo      (Flask)"
    echo "   - mysql    (Base de datos)"
    echo "   - all      (Todos los servicios)"
    exit 1
fi

case "$1" in
    api)
        docker-compose logs -f budgetmap-api
        ;;
    geo)
        docker-compose logs -f budgetmap-geo
        ;;
    mysql)
        docker-compose logs -f mysql
        ;;
    all)
        docker-compose logs -f
        ;;
    *)
        echo "❌ Servicio no válido: $1"
        echo "Servicios disponibles: api, geo, mysql, all"
        exit 1
        ;;
esac
