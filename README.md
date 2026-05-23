ARQUITECTURA FINAL PROPUESTA PARA BUDGETMAP

1️⃣ OBJETIVO DEL PROYECTO
BudgetMap será una plataforma para:
✔ Mostrar lugares poco conocidos (parques, museos, sitios turísticos)
✔ Mostrar eventos en esos lugares (culturales, deportivos, artísticos, veterinarios)
✔ Gestión de establecimientos con registro y aprobación (restaurantes, parqueaderos)
✔ Permitir reservas en establecimientos aprobados
✔ Sistema de PQRS y sugerencias
✔ Uso de roles claramente diferenciados:

ADMIN
MODERADOR
ESTABLECIMIENTO
CLIENTE

Visitantes no registrados → solo vistas públicas

2️⃣ ENTIDADES (MODELO EXACTO NECESARIO)
🟦Usuario
id: Long
username: String
password: String
rol: Rol (ADMIN, MODERADOR, ESTABLECIMIENTO, CLIENTE)
estado: EstadoUsuario (ACTIVO, PENDIENTE, RECHAZADO)
nombre
email
telefono

🟩 Lugar
id
nombre
tipoLugar (PARQUE, MUSEO, SITIO_TURISTICO)
descripcion
ciudad
direccion
latitud
longitud
estado (PUBLICADO, BORRADOR)
creadoPor (MODERADOR)

🟧 Evento
id
titulo
tipoEvento (CULTURAL, DEPORTIVO, ARTISTICO, VETERINARIO)
fechaInicio
fechaFin
descripcion
lugarId
creadoPor (MODERADOR)

🟨 Establecimiento
id
nombre
tipo (RESTAURANTE, PARQUEADERO)
descripcion
ubicacion
estado (PENDIENTE, APROBADO, RECHAZADO)
contacto
horarios
creadoPor (usuario establecimiento)

🟪 Reserva
id
usuarioId (CLIENTE)
establecimientoId
fechaReserva
estado (PENDIENTE, CONFIRMADA, CANCELADA)

🟫 PQRS / Sugerencia
id
usuarioId
tipo (PQRS, SUGERENCIA)
mensaje
estado (ABIERTA, ASIGNADA, RESPONDIDA)
asignadoA (ADMIN/MODERADOR)

3️⃣ FLUJOS POR ROL (FUNCIONALIDAD REAL)

👑 ADMIN
CRUD completo de usuarios
CRUD de lugares
CRUD de establecimientos
Gestionar PQRS (asignar y responder)
Aprobar moderadores y establecimientos
Generar reportes generales

🟦 MODERADOR
CRUD de lugares
CRUD de eventos
Aprobar establecimientos
Ver PQRS asignadas

🟧 ESTABLECIMIENTO
Registrarse → queda PENDIENTE
Cuando esté aprobado:
editar su perfil
administrar reservas
confirmar/cancelar reservas

🟩 CLIENTE
ver lugares públicos
ver eventos de cualquier lugar
hacer reservas en establecimientos aprobados
recibir notificaciones
crear PQRS y sugerencias

🌐 VISITANTE NO REGISTRADO
ver página de inicio
ver lugares públicos
ver eventos
registrarse
iniciar sesión

4️⃣ RUTAS Y VISTAS (REORGANIZADAS Y CORREGIDAS)
Aquí está la estructura correcta de vistas que sí debes tener, basada en Spring Boot + Thymeleaf.

📁 templates/public/
home.html
lugares.html
detalle-lugar.html
evento.html

📁 templates/auth/
login.html
registro.html

📁 templates/admin/
dashboard.html
usuarios.html
reportes.html
lugares.html
establecimientos.html
pqrs.html

📁 templates/moderador/
dashboard.html
lugares.html
crear-lugar.html
editar-lugar.html
eventos.html
establecimientos-pendientes.html

📁 templates/establecimiento/
dashboard.html
perfil.html
reservas.html

📁 templates/cliente/
dashboard.html
reservas.html
pqrs.html

5️⃣ RUTAS CONTROLADAS POR ROLES
🌐 PÚBLICAS
GET /
GET /home
GET /lugares
GET /lugares/{id}
GET /lugares/{id}/eventos
GET /login
GET /registro
POST /registro

👑 ADMIN
GET /admin/dashboard
GET /admin/usuarios
GET /admin/usuarios/crear
POST /admin/usuarios
GET /admin/lugares
GET /admin/establecimientos
GET /admin/reportes
GET /admin/pqrs

🟦 MODERADOR
GET /moderador/dashboard
GET /moderador/lugares
GET /moderador/lugares/crear
POST /moderador/lugares
GET /moderador/eventos
POST /moderador/eventos
GET /moderador/establecimientos/pendientes
POST /moderador/establecimientos/{id}/aprobar

🟧 ESTABLECIMIENTO
GET /establecimiento/dashboard
GET /establecimiento/perfil
POST /establecimiento/perfil
GET /establecimiento/reservas
POST /establecimiento/reservas/{id}/confirmar
POST /establecimiento/reservas/{id}/cancelar

🟩 CLIENTE
GET /cliente/dashboard
GET /cliente/reservas
POST /cliente/reservas
GET /cliente/pqrs
POST /cliente/pqrs
