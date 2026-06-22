# 1. Documento Técnico e Implantación del Sistema

## 1.1. Introducción
El presente documento constituye el Manual Técnico y de Implantación del ecosistema distribuido BudgetMap. Su elaboración responde a la necesidad de formalizar los lineamientos operativos, arquitectónicos y de aseguramiento de calidad (QA) requeridos para el despliegue, configuración y mantenimiento de los distintos microservicios que componen la solución. Este artefacto técnico consolida las topologías de red, esquemas de persistencia y directrices de infraestructura necesarias para garantizar un entorno de ejecución de alta disponibilidad.

## 1.2. Propósito del documento
El propósito de este documento es proporcionar una guía técnica detallada sobre la arquitectura, tecnologías subyacentes, componentes y el proceso de implantación y despliegue del ecosistema BudgetMap. Sirve como referencia central para el equipo de desarrollo, operaciones, QA y arquitectos involucrados en el ciclo de vida del producto.

## 1.3. Alcance del proyecto
El alcance comprende el despliegue del Backend Core (`budgetmap-api`), el microservicio Geo y de Analítica (`budgetmap-geo`), la capa de persistencia (bases de datos MySQL y caché en Redis), y el Frontend híbrido integrado basado en Progressive Web Apps (PWA) con Service Workers. Incluye módulos administrativos, de aliados, de anfitriones y de usuario final.

## 1.4. Listado de módulos
1. **Módulo de Administración (Control Center):** Gestión de roles, KPIs, métricas globales, moderación de locales, aprobación de eventos y PQRS.
2. **Módulo de Aliados:** Gestión de establecimientos comerciales, creación de promociones geolocalizadas, monitoreo en vivo de interacciones.
3. **Módulo de Anfitriones:** Planificación de eventos, aforos, precios dinámicos y portal Check-In nativo web.
4. **Módulo de Explorador (Usuario Final):** Radar dinámico de descubrimiento, sistema de reservas, gamificación (tokens, top 5) y perfil interactivo.
5. **Módulo Geo & Analítica:** Filtros espaciales avanzados y control de tráfico (Rate Limiting).

## 1.5. Objetivo general
Establecer los lineamientos arquitectónicos y de infraestructura necesarios para implantar, operar y mantener el sistema BudgetMap de manera escalable, segura y eficiente.

## 1.6. Objetivos específicos
- Detallar la topología de la solución y las integraciones entre microservicios.
- Enumerar el stack tecnológico validado y configurado en el ecosistema.
- Proveer instrucciones claras para levantar los servicios en entornos locales y productivos.
- Definir pautas de verificación posterior a la implantación para garantizar los estándares de QA.

---

# 2. Arquitectura de la Solución

## 2.1. Arquitectura general
BudgetMap se basa en una arquitectura de **Microservicios** que separa las responsabilidades transaccionales operacionales de las analíticas/geolocalizadas. El flujo principal se maneja a través de un Core monolítico expuesto vía API REST, y delega operaciones de minería de datos y geometría espacial avanzada a un microservicio especializado. El frontend es acoplado estáticamente pero opera bajo un modelo robusto con capacidades offline (PWA) gracias a estrategias híbridas.

## 2.2. Componentes de Software
- **Core API (`budgetmap-api`):** Desarrollado en Java/Spring Boot. Gestiona la lógica de reservas, pagos, seguridad state-less con JWT y roles.
- **Geo Service (`budgetmap-geo`):** Desarrollado en Python/Flask. Maneja el análisis geoespacial de lugares, extracción de reportes analíticos y protección adicional mediante Rate Limiting volumétrico.
- **Frontend App:** Embebido en los recursos estáticos del Core API (`src/main/resources/static`). Funciona interactuando de la mano con Service Workers (`sw.js`) empleando estrategias `Cache-First` y `Network-First`.

## 2.3. Integraciones externas
- Comunicación interna asíncrona y resiliente entre microservicios utilizando **Spring Cloud OpenFeign** con patrones de tolerancias a fallos (Circuit Breakers) administrados por **Resilience4j**.
- Conexión a servicios de correo electrónico y recuperación de contraseñas impulsado por **Brevo** (`spring-boot-starter-mail`).
- Integración de visores cartográficos en el frontend interactuando con los polígonos generados por la base de datos (con soporte de **Hibernate Spatial** y **JTS**).
- Administración y visualización de modelos relacionales de datos y tablas utilizando **DBeaver** como cliente SQL principal.

---

# 3. Tecnologías Utilizadas

## 3.1. Frontend
- **HTML5, Vanilla JS, CSS3**
- **TailwindCSS (v3.4):** Framework de utilidades para estilos responsivos ágiles.
- **Service Workers (PWA):** Gestión de caché y operaciones sin conexión (interceptores de fetch).
- **Chart.js:** Para visualización de métricas en vivo en los dashboards.
- **jsPDF:** Generación nativa de PDFs (tickets, reportes corporativos) exportables desde el navegador.

## 3.2. Backend
- **Core API:** Java 17, Spring Boot 3.2.0, Spring Data JPA.
- **Microservicio Geo:** Python 3.9+, Framework Flask.
- **Seguridad:** Spring Security, JSON Web Tokens (JJWT 0.12.3), Bucket4j para Rate Limiting a nivel de Java, y Flask-Limiter para la capa Python.
- **Comunicaciones y Resiliencia:** OpenFeign, Resilience4j.
- **Manejo de Geometría Espacial:** Hibernate Spatial, JTS Core.

## 3.3. Patrón de desarrollo
- **Microservicios Híbridos y MVC (Modelo-Vista-Controlador)** en la distribución de directorios de Spring Boot.
- **Mobile-First Development:** Interfaces de usuario adaptadas a la interacción táctil (swipes, navbars horizontales dinámicas, protección anti-overflow).
- **RESTful API Design:** Comunicación JSON ligera y state-less entre cliente y capas de servicios.

## 3.4. Bases de datos
- **MySQL 8+:** Base de datos relacional principal con características habilitadas para datos espaciales. La base de datos productiva se encuentra alojada y gestionada mediante **Aiven**.
- **Redis:** Capa de caché en memoria de alta velocidad (`spring-boot-starter-data-redis`). El clúster de caché se encuentra desplegado utilizando la infraestructura de **Railway**.

## 3.5. Especificaciones técnicas de los servidores
- Sistema operativo recomendado: Distribuciones Linux optimizadas para nube (ej. Ubuntu Server 20.04/22.04 LTS o imágenes Docker Alpine).
- Máquina virtual de Java (JVM) versión 17 (OpenJDK).
- Entorno Python 3.9+ con entorno virtual (`virtualenv`).
- Base de datos MySQL 8 con collation `utf8mb4_unicode_ci`.
- Servidor Redis 6+.

---

# 4. Servidores e Implantación

## 4.1. Servidores y configuración
El sistema ha sido desplegado exitosamente utilizando la plataforma PaaS de **Render** para asegurar escalabilidad y un ciclo continuo de entrega.
- **Backend Core:** Empaquetado nativo mediante el wrapper de Maven (`mvnw`) a un archivo autoejecutable `.jar` y desplegado como Web Service en Render.
- **Servicio Geo:** Ejecución sobre Gunicorn productivo anclado a la aplicación Flask, configurado como Web Service secundario en Render.

## 4.2. Dependencias necesarias para despliegue
**Para el Core Java (`budgetmap-api`):**
1. Instalar Java 17 JDK.
2. Inyectar variables de entorno de producción (credenciales DB, URI Redis, secrets de JWT, tokens de correo SMTP).

**Comandos de implantación Java (Build & Run):**
```bash
cd budgetmap-api
mvn clean install -U
java -jar target/budgetmap-api-1.0.0.jar
```
*(El portal principal se levanta en un puerto definido, comúnmente el 8080).*

**Para el Servicio Geo (`budgetmap-geo`):**
1. Python 3.9+ instalado y activo en el path.

**Comandos de implantación Python:**
```bash
cd budgetmap-geo
pip install -r requirements.txt
python app.py
```
*(Expone servicios analíticos generalmente en el puerto 5000).*

## 4.3. Verificación de la implantación (Criterios QA)
Tras el despliegue a los distintos entornos (Staging/Producción), el equipo de QA validará la implantación de la siguiente forma:
1. **Health Checks:** Acceder a la URL raíz y endpoints de estado de salud (si están habilitados por Actuator) para asegurar un código `HTTP 200 OK`.
2. **Carga Estática y Service Worker:** Navegar al panel de la aplicación, abrir *Chrome DevTools > Application* y asegurar que `sw.js` figura como activo e instalando la caché en el `Cache Storage`.
3. **Comunicación Inter-servicios (Ping API <-> Geo):** Realizar una petición transaccional que dispare la búsqueda de locaciones (`/api/geo/ubicacion`). No deben presentarse errores en los logs provenientes de FeignClient.
4. **Test de Resiliencia (Rate Limiting):** Lanzar múltiples peticiones automatizadas (ej. con Apache JMeter o Postman) para corroborar la recepción de errores HTTP `429 Too Many Requests` protegiendo al sistema.

---

# 5. Anexos
- Documentos de Diseño UX/UI (Figma u otro si corresponde).
- Diagramas lógicos de base de datos (Entidad-Relación y diagramas espaciales).
- Scripts de despliegue automatizado o archivos `Dockerfile`/`docker-compose.yml`.

# 6. Definiciones de Acrónimos y Glosario

## 6.1. Términos de Negocio y Producto
- **Aliado:** Establecimiento comercial afiliado a BudgetMap que ofrece sus servicios y promociones dentro del ecosistema.
- **Anfitrión:** Organizado de eventos que utiliza la plataforma para gestionar aforos, planeación y control de acceso (Check-In).
- **BudgetMap:** Plataforma integral e inteligente enfocada en democratizar el acceso al entretenimiento y turismo local.
- **Check-In (Módulo):** Interfaz Web/App nativa diseñada para que los anfitriones escaneen códigos de ticket y controlen la asistencia en portería de forma instantánea.
- **Dashboard:** Panel de control adaptativo (Mobile-First) donde administradores, aliados y anfitriones gestionan su actividad.
- **Explorador:** Usuario final de la aplicación, encargado de descubrir lugares, realizar reservas y asistir a eventos.
- **Gamificación:** Sistema de recompensas de la plataforma para fidelizar usuarios mediante mecánicas de juego.
- **Leaderboard:** Tabla de clasificación en tiempo real que premia la recurrencia de los Exploradores mostrando el "Top 5" de la comunidad.
- **PQRS:** Peticiones, Quejas, Reclamos y Sugerencias.
- **Radar Dinámico:** Motor de descubrimiento geoespacial para que los usuarios encuentren lugares y eventos ocultos cercanos a su ubicación.
- **Token (Moneda virtual):** Elemento de recompensa otorgado a los Exploradores por sus interacciones continuas en la plataforma.

## 6.2. Términos Técnicos y de Infraestructura
- **Aiven:** Proveedor de servicios en la nube utilizado para el alojamiento, despliegue y gestión manejada de la base de datos MySQL.
- **API (Application Programming Interface):** Interfaz que permite la comunicación y transferencia de datos entre el frontend y los microservicios.
- **Backend Core (`budgetmap-api`):** Microservicio principal del sistema, desarrollado en Java/Spring Boot, que maneja la persistencia y la lógica transaccional y de seguridad.
- **Brevo:** Plataforma externa (SaaS) empleada para la gestión del envío de correos electrónicos transaccionales y recuperación de contraseñas.
- **Bucket4j:** Librería Java utilizada para implementar estrategias de Rate Limiting volumétrico en la API.
- **Cache-First / Network-First:** Estrategias implementadas en el Service Worker de la PWA para priorizar la carga de la caché local o de la red según la disponibilidad de conexión.
- **Circuit Breaker (Resilience4j):** Patrón de diseño de software utilizado para detectar fallos y encapsular la lógica de prevención de errores en la comunicación entre microservicios.
- **DBeaver:** Cliente de software utilizado como administrador de base de datos universal y visualizador de esquemas relacionales.
- **Flask:** Micro-framework de Python utilizado para construir el servicio Geo y Analítico.
- **Geo Service (`budgetmap-geo`):** Microservicio especializado en el procesamiento de datos geoespaciales, analítica y filtros complejos.
- **Gunicorn:** Servidor HTTP WSGI para Python utilizado para desplegar el entorno de producción del microservicio Geo.
- **Hibernate Spatial / JTS:** Extensiones tecnológicas encargadas de facilitar el mapeo, manipulación y consulta de datos y geometrías espaciales en Java.
- **JPA (Java Persistence API):** Especificación de Java empleada para acceder, persistir y administrar datos entre objetos y la base de datos relacional.
- **JWT (JSON Web Token):** Estándar abierto (RFC 7519) utilizado para transmitir información de identidad de forma segura y state-less entre las partes como un objeto JSON.
- **Microservicios:** Arquitectura de software en la que la aplicación principal se compone de servicios pequeños, independientes y comunicados por red.
- **Mobile-First:** Filosofía de diseño y desarrollo frontend en la cual el sistema se optimiza principalmente para pantallas táctiles de dispositivos móviles.
- **MVC (Model-View-Controller):** Patrón de arquitectura de software que separa los datos, la lógica de negocio y las interfaces de usuario.
- **OpenFeign:** Cliente REST declarativo de Spring Cloud usado para facilitar la comunicación interna entre microservicios de forma asíncrona.
- **PaaS (Platform as a Service):** Plataforma como Servicio en la nube; modelo donde se aloja el entorno de despliegue de las aplicaciones (ej. Render).
- **PWA (Progressive Web App):** Aplicación web que utiliza capacidades modernas (Service Workers) para ofrecer una experiencia similar a una app nativa, incluyendo funcionalidades offline.
- **Railway:** Plataforma de infraestructura en la nube (PaaS) utilizada para aprovisionar y desplegar el clúster de base de datos en memoria Redis.
- **Rate Limiting:** Estrategia arquitectónica que limita el tráfico de red de un cliente o IP en un tiempo determinado, previniendo ataques de denegación de servicio (DDoS).
- **Redis:** Motor de base de datos en memoria, utilizado como capa de caché de alta velocidad.
- **Render:** Plataforma en la nube (PaaS) donde están desplegados exitosamente el Core y el servicio Geo para entrega continua y escalabilidad.
- **Service Worker:** Script en segundo plano que se ejecuta en el navegador web y actúa como proxy de red, interceptando peticiones para habilitar PWA y caché.
- **SPA (Single Page Application):** Aplicación web que interactúa asíncronamente con el servidor reescribiendo la página actual en lugar de cargar páginas enteras.
- **TailwindCSS:** Framework de CSS basado en clases de utilidad (utility-first) usado para maquetar el frontend responsivo del ecosistema.
- **WSGI (Web Server Gateway Interface):** Especificación de interfaz estándar en Python que comunica el servidor web con la aplicación web (Flask).

# 7. Documentos de Referencia

## 7.1. Documentación Interna del Proyecto
- Archivo `README.md` del ecosistema.
- Matriz de Calidad: `AUDITORIA_EXHAUSTIVA_ISO25010_v1.md`.
- Arquitectura Macro: `DOCUMENTO DE ARQUITECTURA.docx`.
- Pruebas de API: `budgetmap_postman_collection.json` y `DOCUMENTO_PRUEBAS_API.md`.
- Estrategia de caching: `sw.js` (Líneas base para la PWA).

## 7.2. Manuales y Documentación Oficial (Stack Tecnológico)
A continuación, se listan los recursos oficiales que sirvieron como base técnica para el desarrollo y la estructuración de la plataforma:

**Backend (Java Core):**
- [Java 17 (Documentación Oficial de Oracle)](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot 3.x Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Resilience4j Documentación Oficial](https://resilience4j.readme.io/)
- [JSON Web Token (JJWT) GitHub](https://github.com/jwtk/jjwt)
- [Bucket4j (Rate Limiting en Java)](https://bucket4j.com/)
- [Hibernate Spatial & JTS (Documentación oficial)](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)

**Microservicio Geo y Analítica (Python):**
- [Python 3.9+ Official Documentation](https://docs.python.org/3/)
- [Flask Framework Docs](https://flask.palletsprojects.com/)
- [SQLAlchemy 2.0 Documentation](https://docs.sqlalchemy.org/)
- [Flask-Limiter](https://flask-limiter.readthedocs.io/)
- [Gunicorn WSGI Server](https://docs.gunicorn.org/)

**Frontend y Visualización:**
- [MDN Web Docs: HTML5, JavaScript & CSS](https://developer.mozilla.org/)
- [TailwindCSS v3.4 Documentation](https://tailwindcss.com/docs)
- [Service Workers & PWA (MDN / Google Developers)](https://web.dev/explore/progressive-web-apps)
- [Chart.js Documentation](https://www.chartjs.org/docs/latest/)
- [jsPDF Documentation](https://artskydj.github.io/jsPDF/docs/jsPDF.html)

**Bases de Datos, Herramientas e Infraestructura:**
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- [Redis Official Documentation](https://redis.io/docs/)
- [DBeaver (Cliente SQL Universal)](https://dbeaver.io/docs/wiki/)
- [Render Docs (PaaS Deployment)](https://docs.render.com/)
- [Aiven Docs (Managed MySQL)](https://docs.aiven.io/)
- [Railway Docs (Managed Redis)](https://docs.railway.app/)
- [Brevo API & Documentación de SMTP](https://developers.brevo.com/)
