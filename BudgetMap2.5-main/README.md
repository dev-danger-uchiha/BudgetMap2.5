# 🚀 BudgetMap Ecosystem

> **BudgetMap** es una plataforma integral e inteligente diseñada para democratizar el acceso al entretenimiento, turismo local y eventos, eliminando sobrecostos. Conecta a usuarios exploradores con establecimientos comerciales (Aliados) y organizadores de eventos (Anfitriones) a través de un ecosistema seguro, gamificado y altamente responsivo.

![Microservicios](https://img.shields.io/badge/Arquitectura-Microservicios-blue)
![Spring Boot](https://img.shields.io/badge/Core_API-Spring_Boot_3-brightgreen.svg)
![Python Flask](https://img.shields.io/badge/Geo_Service-Python_Flask-yellow.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-3.4-38B2AC.svg)
![License](https://img.shields.io/badge/Licencia-Privada-blue.svg)

---

## 🏛️ Arquitectura del Sistema (Microservicios)

BudgetMap utiliza una arquitectura distribuida para maximizar el rendimiento y separar responsabilidades. El ecosistema se compone de dos grandes módulos:

### 1. 🟢 `budgetmap-api` (Core Monolith & Frontend)
Este es el motor principal del sistema. Desarrollado en **Java 17 y Spring Boot 3**, maneja la seguridad, persistencia principal y renderizado del cliente.
- **Seguridad:** Spring Security con Tokens JWT (JSON Web Tokens) sin estado.
- **Persistencia:** MySQL 8+ vía Spring Data JPA y pool de conexiones HikariCP.
- **Frontend Híbrido:** HTML5 + Vanilla JS + TailwindCSS. Diseñado bajo el enfoque "Mobile-First", incluye gráficos analíticos (`Chart.js`) y generación nativa de reportes (`jsPDF`).
- **Lógica de Negocio:** Gestión de Roles, Reservas, Creación de Eventos, Ticketing y Pagos.

### 2. 🌍 `budgetmap-geo` (Geo & Analytics Microservice)
Microservicio especializado en análisis geoespacial, filtros complejos y límites de peticiones (Rate Limiting).
- **Framework:** Python Flask.
- **Base de Datos:** SQLAlchemy para consultas avanzadas y reportes analíticos.
- **Seguridad / Rate Limiting:** Flask-Limiter para proteger las API contra abusos volumétricos (ej. endpoints limitados a peticiones por minuto).
- **Rutas Principales:** `/api/geo` (Geolocalización espacial), `/api/filtros` (Filtros dinámicos), y `/api/reportes` (Extracción y minería de datos).

---

## 🌟 Características Principales y Roles

El ecosistema se divide en 4 perfiles principales, con paneles (Dashboards) adaptables 100% a dispositivos móviles:

### 1. 🛡️ Panel Administrativo (Control Center)
- **Inteligencia de Negocio:** Estadísticas globales en tiempo real utilizando `Chart.js` (Evolución de usuarios, Composición de establecimientos, Métricas financieras).
- **Reportes Profesionales:** Exportación milimétrica a PDF corporativo utilizando coordenadas en `jsPDF`.
- **Gestión Integral:** Aprobación de locales y eventos, moderación de usuarios y sistema de PQRS (Peticiones, Quejas, Reclamos y Sugerencias).

### 2. 🏪 Panel de Aliados
- **Gestión de Locales:** Administración de información, aforo y geolocalización de establecimientos.
- **Creación de Promociones:** Motor para publicar ofertas dinámicas que el motor geoespacial distribuye en la zona.
- **Radar Comercial:** Monitoreo de interacciones de clientes y control de reservas en vivo.

### 3. 🎟️ Panel de Anfitriones
- **Gestión de Eventos:** Planificación de eventos con aforo estricto y precios dinámicos.
- **Check-In Mobile-First:** Módulo web-app nativo que permite escanear códigos de ticket en la entrada para validación instantánea y control de asistencia en portería.

### 4. 🧭 Experiencia del Explorador (Usuario Final)
- **Radar Dinámico:** Motor de descubrimiento para hallar lugares ocultos impulsado por el microservicio Geo.
- **Ahorro e Interactividad:** Reservas con cálculo de descuentos automáticos.
- **Gamificación:** Leaderboard en tiempo real que premia la recurrencia con Tokens y un Top 5 de la comunidad.

---

## 📂 Estructura del Repositorio

```text
PROYECTO REAL/
├── budgetmap-api/         # ☕ Microservicio Core Java (Spring Boot)
│   ├── src/main/java/     # Lógica transaccional, Modelos, Controladores
│   ├── src/main/resources/
│   │   ├── static/        # Vistas Frontend de todos los roles (Tailwind + JS)
│   │   └── application.properties
│   └── pom.xml
├── budgetmap-geo/         # 🐍 Microservicio Geo y Analítica (Python)
│   ├── app.py             # Inicialización de Flask y Rate Limiting
│   ├── routes/            # Blueprint endpoints (geo_routes, alert_routes)
│   ├── services/          # Lógica analítica y cruce de datos
│   └── requirements.txt
├── database/              # Dumps y esquemas de base de datos SQL
├── scripts/               # Scripts de soporte o mantenimiento
└── README.md              # Este archivo global
```

---

## 🚀 Instalación y Puesta en Marcha

Dado que el proyecto maneja microservicios, debes levantar ambos para la funcionalidad completa:

### 1. Levantar la Base de Datos
- Asegúrate de tener **MySQL 8+** activo y crea la base de datos principal:
  ```sql
  CREATE DATABASE budgetmap CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

### 2. Levantar API Principal (Java)
```bash
cd budgetmap-api
mvn clean install -U
mvn spring-boot:run
```
> La API y el Portal Web estarán disponibles en: **https://budgetmap-api.onrender.com/**

### 3. Levantar Microservicio Geo (Python)
Requiere Python 3.9+ instalado:
```bash
cd budgetmap-geo
pip install -r requirements.txt
python app.py
```
> El servicio Geo y analítica estará corriendo en: **http://localhost:5000/**

---

## 📱 Ecosistema Ultra-Responsivo
Todo el frontend ubicado en `budgetmap-api/src/main/resources/static` está programado bajo un estándar "Mobile-First":
- **Navbars Híbridas y Swipes:** Las barras de navegación superiores se transforman en carruseles horizontales ocultos y listados de iconos adaptables, garantizando navegación fluida a un dedo.
- **Tablas Fluidas:** Protección anti-overflow en listados masivos.
- **Vistas Dedicadas:** El Módulo Check-In de anfitriones elimina todo el ruido de la pantalla para convertir cualquier dispositivo en un escáner industrial inmersivo.

---

**BudgetMap Team © 2026** - *Innovación en Economía Local, Gamificación y Eventos*
