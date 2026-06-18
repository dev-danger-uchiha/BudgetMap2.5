# Documentación de Pruebas de API - BudgetMap

Este documento describe cómo importar y ejecutar la colección completa de pruebas de API de BudgetMap en **Postman**, abarcando la configuración tanto para el entorno **Local** como para **Producción (Despliegue)**.

---

## 1. Importar la Colección

1. Abre **Postman**.
2. En la barra lateral izquierda o en la esquina superior izquierda, haz clic en **Import**.
3. Selecciona la opción **File** o arrastra el archivo `budgetmap_postman_collection.json` que se encuentra en la raíz de tu proyecto (`c:\PROYECTO REAL\budgetmap_postman_collection.json`).
4. Haz clic en **Import**.

Verás que se crea una nueva colección llamada **"BudgetMap API Completa"** con múltiples carpetas correspondientes a cada módulo de tu código (Auth, Usuarios, Eventos, Lugares, etc.).

---

## 2. Configuración de Entornos (Local y Producción)

La colección fue diseñada de forma dinámica utilizando la variable `{{base_url}}` en todas sus peticiones. Para cambiar entre el servidor local y el que está en la nube, debes configurar **Entornos (Environments)** en Postman.

### A. Entorno Local
1. En Postman, ve a la esquina superior derecha y haz clic en el icono del **Ojo** (Environment quick look) o ve a la pestaña **Environments**.
2. Haz clic en **Add** (Añadir).
3. Nombra el entorno como **"BudgetMap - Local"**.
4. Añade una variable:
   - **VARIABLE:** `base_url`
   - **INITIAL VALUE:** `http://localhost:8080`
   - **CURRENT VALUE:** `http://localhost:8080`
5. (Opcional) Añade otra variable llamada `jwt_token` y déjala vacía. Se llenará automáticamente al hacer Login.
6. Guarda el entorno.

### B. Entorno de Producción / Despliegue
1. Siguiendo los pasos anteriores, crea un nuevo entorno llamado **"BudgetMap - Producción"**.
2. Añade la variable:
   - **VARIABLE:** `base_url`
   - **INITIAL VALUE:** `https://budgetmap-api.onrender.com`
   - **CURRENT VALUE:** `https://budgetmap-api.onrender.com`
3. Guarda el entorno.

### ¿Cómo cambiar entre ellos?
En la esquina superior derecha de tu ventana principal de Postman hay un menú desplegable. Solo tienes que seleccionar "BudgetMap - Local" cuando estés ejecutando el backend en tu PC, o "BudgetMap - Producción" para hacer pruebas con el sistema en vivo en Render.

---

## 3. Autorización y Seguridad Automática

Casi todas las rutas del proyecto están protegidas y requieren un **Bearer Token**.

Para que no tengas que copiar y pegar el token manualmente en cada una de las más de 60 peticiones:
1. Ve a la carpeta **Auth**.
2. Ejecuta la petición **login (POST)** asegurándote de usar credenciales válidas en la pestaña *Body*.
3. Postman capturará el token de la respuesta de manera automática (mediante un script de pruebas que he preconfigurado) y lo guardará en la variable global/colección `jwt_token`.
4. El resto de las peticiones heredarán este token gracias a la configuración de Autenticación de la colección.

---

## 4. Ejecución de Pruebas (Runner)

Si deseas probar todo el sistema de una sola vez:
1. Haz clic derecho sobre el nombre de la colección **BudgetMap API Completa**.
2. Selecciona **Run collection**.
3. Selecciona tu entorno (Local o Producción).
4. Haz clic en **Run BudgetMap API Completa**.
5. Obtendrás un reporte que indicará qué peticiones devolvieron 200 OK y cuáles fallaron (400, 401, 500, etc.).

> [!TIP]
> Recuerda que para peticiones de tipo `POST` y `PUT` (como creación de eventos o lugares) la colección tiene cuerpos genéricos JSON vacíos (`{}`). Deberás ajustarlos según el modelo de datos exacto antes de probar esa ruta en específico.

---

## 5. Casos y Estructura Extraída

La colección generada contiene de forma exhaustiva todos los endpoints mapeados a través de las siguientes controladoras:
*   Auth Controller (Login, Registro)
*   Usuario Controller (Perfiles, Leaderboard, Actualización de Roles)
*   Establecimiento / Lugar Controller (Aprobaciones, Detalles, Radar)
*   Evento Controller (Creación, Aprobación, Mis Eventos)
*   Reserva Controller (Reserva de Mesas, Entradas)
*   AnaliticaLocal Controller (Gestión de métricas del Dashboard)
*   Pasarela Controller (Integración de Pagos)
*   PQRS Controller
*   Y todos los demás controladores existentes.

### Entregable

Este documento, junto con el archivo `budgetmap_postman_collection.json`, constituye la evidencia de las pruebas automatizadas de la API para su entrega final.
