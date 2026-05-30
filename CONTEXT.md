# AutoReach — Contexto del Proyecto

## ¿Qué es este proyecto?

**AutoReach** es una aplicación de escritorio para Windows desarrollada en **JavaFX**.
Es el software de gestión interna de un concesionario de vehículos.
Lo usan los operadores/agentes del concesionario, no los clientes finales.

---

## ¿Qué puede hacer la app?

| Módulo | Descripción |
|---|---|
| **Login de agente** | El operador inicia sesión con username + contraseña |
| **Gestión de clientes** | Crear, editar, eliminar y buscar clientes. También enviar invitación de acceso al portal web |
| **Alquiler de vehículos** | Registrar, editar y borrar alquileres. Ver alquileres registrados |
| **Compra de vehículos** | El cliente compra un vehículo disponible. Genera documentos y transacciones |
| **Ingresos (inventario)** | Añadir, editar y borrar vehículos del inventario. Ver gráfico de coches por año |
| **Informes PDF** | Descargar reportes del inventario y del vehículo en PDF |
| **Documentos** | Ver y firmar/cancelar documentos de transacciones |
| **Pagos pendientes** | Ver transacciones con pago pendiente y actualizar su estado |
| **Activar cuenta cliente** | Enviar email de invitación a Supabase Auth para que el cliente active su acceso web |

---

## Arquitectura general

```
App JavaFX (escritorio Windows)
    │
    │  HTTP REST (java.net.http.HttpClient)
    │  JSON (Gson)
    │  Bearer JWT token
    ▼
API Node.js (http://35.202.97.203:3000)
    │
    ▼
Supabase (PostgreSQL en la nube)
```

- La app JavaFX **nunca habla directamente con la base de datos**.
- Toda la lógica de negocio y acceso a datos pasa por la API Node.js.
- La API Node.js usa Supabase como base de datos PostgreSQL.

---

## Tecnologías del proyecto JavaFX

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 (Microsoft OpenJDK) | Lenguaje principal |
| JavaFX | 21.0.5 | Interfaz gráfica de escritorio |
| Gson | 2.10.1 | Serializar/deserializar JSON |
| AtlantaFX | 2.0.1 | Tema base de la interfaz |
| ControlsFX | 11.2.1 | Componentes UI extra |
| JasperReports | 7.0.1 | Generación de informes PDF |
| OpenHTMLToPDF | 1.0.10 | Exportar HTML con gráficas a PDF |
| Gradle | 8.14 | Build tool |
| jpackage | JDK 17 built-in | Genera el instalador .exe de Windows |
| WiX Toolset | v3.x | Requerido por jpackage para crear .exe |

---

## Estructura de carpetas relevante

```
app/
├── build.gradle                        — configuración del build
├── src/main/java/com/javafx/
│   ├── App/
│   │   ├── App.java                    — punto de entrada JavaFX
│   │   ├── controladorApp.java         — controlador principal (pantalla1.fxml)
│   │   ├── controladorCompra.java      — lógica de compra
│   │   ├── controladorEmergente.java   — ventana de confirmación de compra
│   │   ├── controladorExitoCompra.java — ventana de éxito tras compra
│   │   ├── controladorPagos.java       — ventana de pagos pendientes
│   │   ├── controladorDocumentos.java  — ventana de documentos
│   │   ├── cambiarColores.java         — gestiona el botón activo del sidebar
│   │   ├── GeneradorInformes.java      — genera PDFs con JasperReports/HTML
│   │   └── AlertaEstilizada.java       — diálogos de alerta personalizados
│   ├── JDBC/
│   │   └── Conexion.java               — TODA la comunicación con la API REST
│   └── Model/
│       ├── Cliente.java
│       ├── Coche.java / CocheCompra.java / CocheCompleto.java / Disponible.java
│       ├── Alquiler.java
│       ├── Transaccion.java
│       ├── Documento.java
│       └── ValidadorCliente/Coche/Alquiler.java — validaciones de formularios
└── src/main/resources/
    ├── pantalla1.fxml                  — pantalla principal (sidebar + todos los paneles)
    ├── ventanaInfo.fxml                — confirmación de compra
    ├── ventanaExitoCompra.fxml         — éxito de compra
    ├── ventanaPagos.fxml               — pagos pendientes
    ├── ventanaDocumentos.fxml          — documentos
    ├── styles.css                      — tema visual completo
    └── Application.properties          — URL de la API
```

---

## Clase Conexion.java — la más importante

`com.javafx.JDBC.Conexion` centraliza **toda** la comunicación con la API.

- Usa `java.net.http.HttpClient` para hacer peticiones HTTP.
- Tras el login guarda el **JWT token** del agente y lo añade automáticamente como `Authorization: Bearer <token>` en todas las peticiones.
- Métodos principales:
  - `realizarConexion()` — inicializa el cliente HTTP con la URL de `Application.properties`
  - `login(username, password)` — autentica al agente
  - `mostrarClientes()` — GET /clientes
  - `crearCliente(...)` — POST /clientes
  - `editarCliente(c)` — PUT /clientes/{id}
  - `eliminarCliente(c)` — DELETE /clientes/{id}
  - `registrarAuth(email)` — POST /clientes/registrar-auth (envía invitación Supabase Auth)
  - `mostrarCoches()` — GET /coches (solo disponibles, no vinculados a transacciones)
  - `confirmarAlquiler(...)` — POST /alquileres
  - `efectuarCompra(...)` — POST /transacciones

---

## API Node.js — endpoints relevantes

**Base URL:** `http://35.202.97.203:3000/api`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /agentes/login | Login del operador (username + bcrypt) → JWT propio |
| GET | /clientes | Listar clientes |
| POST | /clientes | Crear cliente |
| PUT | /clientes/:id | Editar cliente |
| DELETE | /clientes/:id | Eliminar cliente (CASCADE en transacciones y documentos) |
| POST | /clientes/registrar-auth | Invitar cliente a Supabase Auth |
| POST | /clientes/login | Login del cliente (email + Supabase Auth) → JWT Supabase |
| GET | /coches | Listar vehículos |
| POST | /coches | Crear vehículo |
| PUT | /coches/:id | Editar vehículo |
| DELETE | /coches/:id | Eliminar vehículo |
| POST | /alquileres | Registrar alquiler |
| PUT | /alquileres/:id | Editar alquiler |
| DELETE | /alquileres/:id | Borrar alquiler |
| POST | /transacciones | Registrar compra/venta |
| GET | /transacciones/pendientes | Transacciones con pago pendiente |
| PUT | /transacciones/:id | Actualizar estado de pago |
| GET | /documentos | Listar documentos |
| PUT | /documentos/:id/firmar | Firmar documento |
| PUT | /documentos/:id/cancelar | Cancelar documento |

---

## Base de datos Supabase — tablas principales

| Tabla | Descripción |
|---|---|
| `agentes` | Operadores de la app (username + password_hash bcrypt) |
| `clientes` | Clientes del concesionario |
| `coches` | Inventario de vehículos |
| `estados_vehiculo` | Estados posibles: disponible, alquilado, vendido |
| `alquileres` | Registros de alquiler |
| `transacciones` | Compras/ventas realizadas |
| `documentos` | Documentos generados por transacciones |

**Relaciones con CASCADE:**
- `transacciones.cliente_id` → `clientes.id` ON DELETE CASCADE
- `documentos.transaccion_id` → `transacciones.id` ON DELETE CASCADE
- `documentos.cliente_id` → `clientes.id` ON DELETE CASCADE

---

## Autenticación — dos flujos distintos

### Agente (operador de la app JavaFX)
- Credenciales: `username` + `password`
- La API compara con bcrypt contra `agentes.password_hash`
- Devuelve un **JWT propio** firmado con `JWT_SECRET` (8h de expiración)
- El token se guarda en `Conexion.agenteToken` y se usa en todas las peticiones

### Cliente (acceso al portal web)
- Credenciales: `email` + `password`
- La API delega en **Supabase Auth** (`supabase.auth.signInWithPassword`)
- Solo funciona si el email pertenece a un registro en la tabla `clientes`
- El operador activa la cuenta del cliente desde la app JavaFX pulsando "Activar Cuenta"
- Supabase envía un email al cliente para que configure su contraseña

---

## Tema visual

- **Sidebar:** navy oscuro `#1a1a2e`
- **Acento primario:** cyan `#0076a8`
- **Fondo de pantallas:** gris azulado `#f0f4f8`
- **Cards:** fondo blanco con borde izquierdo cyan de 3px
- **Botón activo del sidebar:** fondo `rgba(0,212,255,0.13)` + texto blanco
- **Botones de acción:** cyan `#0076a8` (principal), rojo `#c0392b` (salir/eliminar)
- El tema está definido en `styles.css` (reglas CSS de JavaFX con prefijo `-fx-`)
- Los colores inline de los FXML complementan el CSS

---

## Generación del instalador

```powershell
# Desde la raíz del proyecto
.\gradlew.bat :app:copyDependencies :app:copyJavaFXLibs :app:compileJava :app:processResources :app:classes :app:jar :app:buildForInstaller :app:jpackage -x clean
```

Genera:
- `app/build/jpackage/AutoReach-1.0.0.exe` — instalador
- `app/build/jpackage/AutoReach/AutoReach.exe` — ejecutable directo

**Requisitos para buildear:**
- JDK 17 (Microsoft OpenJDK)
- WiX Toolset v3.x
- Conexión a internet (primera vez, para descargar dependencias)

---

## Fichero de configuración

`app/src/main/resources/Application.properties`
```properties
api.url=http://35.202.97.203:3000
```
Este es el único punto de configuración. Cambiar esta URL afecta a todas las peticiones de la app.

---

## Notas importantes para el desarrollo

1. **No modificar la lógica en los controladores sin entender el flujo.** `controladorApp.java` tiene más de 3000 líneas y gestiona todos los paneles desde una sola pantalla (StackPane con Panes visibles/ocultos).
2. **Los estilos inline en FXML tienen prioridad sobre styles.css.** Si un elemento no cambia de color con CSS, busca su `style=` en el FXML.
3. **Los informes PDF** se guardan en `C:\Users\<usuario>\Documents\AutoReach\`.
4. **Los coches mostrados en ingresos y alquiler** solo son los con estado DISPONIBLE y sin transacción activa.
5. **El `clean` de Gradle puede fallar** si Windows tiene bloqueada la carpeta `build/jpackage`. Usar `-x clean` como workaround.
