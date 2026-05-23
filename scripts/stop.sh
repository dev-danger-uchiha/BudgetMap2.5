#!/bin/bash

# BudgetMap - Script de detención

echo "🛑 Deteniendo BudgetMap..."

cd "$(dirname "$0")/../docker" || exit

docker-compose down

echo "✅ BudgetMap detenido"
