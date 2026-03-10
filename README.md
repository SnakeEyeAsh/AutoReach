# AutoReach - Sistema de Gestion de Vehiculos

Aplicacion JavaFX para la gestion integral de vehiculos: inventario, alquileres, compras y generacion de informes PDF.

## Requisitos Previos

| Requisito | Version |
|-----------|---------|
| **Java JDK** | 24 o superior |
| **Gradle** | 8.8+ (o usar el wrapper `./gradlew`) |
| **Conexion a internet** | Necesaria para acceder a la BD en Supabase |
| **Puerto 5432** | Abierto en el firewall (PostgreSQL) |
| **Resolucion de pantalla** | Minimo 850x600 |

## Tecnologias

- **JavaFX 25** - Interfaz grafica
- **PostgreSQL** (Supabase) - Base de datos remota con SSL
- **JasperReports 7.0.1** - Generacion de informes PDF
- **OpenHTMLToPDF 1.0.10** - Conversion HTML a PDF
- **ControlsFX 11.2.1** - Controles avanzados de UI
- **AtlantaFX 2.0.1** - Tema visual (NordDark)
- **Gradle** - Sistema de build y gestion de dependencias

## Estructura del Proyecto

```
AppInterfaces/
├── app/
│   ├── build.gradle                          # Configuracion de build y dependencias
│   └── src/main/
│       ├── java/com/javafx/
│       │   ├── App/                          # Controladores y logica de UI
│       │   │   ├── App.java                  # Punto de entrada de la aplicacion
│       │   │   ├── controladorApp.java       # Controlador principal (todas las pestanas)
│       │   │   ├── controladorCompra.java    # Controlador del formulario de compra
│       │   │   ├── controladorEmergente.java # Controlador de ventana modal/info
│       │   │   ├── GeneradorInformes.java    # Generacion de PDFs
│       │   │   ├── AlertaEstilizada.java     # Dialogos estilizados
│       │   │   ├── AlertaFallo.java          # Alertas de error
│       │   │   └── cambiarColores.java       # Utilidades de tema/colores
│       │   ├── Model/                        # Modelos de datos y validadores
│       │   │   ├── Cliente.java              # Entidad cliente
│       │   │   ├── Coche.java                # Entidad vehiculo basico
│       │   │   ├── CocheCompra.java          # Modelo vehiculo para compra
│       │   │   ├── CocheCompleto.java        # Vehiculo con todos los campos
│       │   │   ├── Alquiler.java             # Entidad transaccion de alquiler
│       │   │   ├── Disponible.java           # Modelo de disponibilidad
│       │   │   ├── ValidadorCliente.java     # Validacion de formulario cliente
│       │   │   ├── ValidadorCoche.java       # Validacion de formulario vehiculo
│       │   │   └── ValidadorAlquiler.java    # Validacion de formulario alquiler
│       │   ├── JDBC/                         # Capa de acceso a datos
│       │   │   ├── Conexion.java             # DAO con operaciones CRUD
│       │   │   └── Config.java               # Carga de Application.properties
│       │   └── Data/
│       │       └── Datos.java                # Constantes y enums
│       └── resources/
│           ├── Application.properties        # Configuracion de base de datos
│           ├── styles.css                    # Estilos CSS de la aplicacion
│           ├── pantalla1.fxml                # Layout principal (pestanas)
│           ├── ventanaInfo.fxml              # Ventana de informacion
│           ├── Assets/                       # Imagenes e iconos PNG
│           │   └── pantallaCompra.fxml       # Formulario de compra
│           └── informes/                     # Plantillas Jasper (.jrxml/.jasper)
├── Informer/                                 # Directorio de salida de PDFs generados
├── gradle/                                   # Wrapper de Gradle
└── settings.gradle
```

## Configuracion

### Base de Datos

La configuracion de conexion a la base de datos se encuentra en:

```
app/src/main/resources/Application.properties
```

Contenido esperado:

```properties
db.url=jdbc:postgresql://<host>:<puerto>/<base_de_datos>
db.user=<usuario>
db.password=<contraseña>
db.ssl=require
```

La carga se realiza mediante **classpath** (`getResourceAsStream`), por lo que no se usan rutas absolutas.

> **Nota:** Este archivo se empaqueta dentro del JAR. Para cambiar la configuracion de BD en produccion sin recompilar, se recomienda externalizar este archivo.

### Esquema de Base de Datos

La aplicacion requiere las siguientes tablas en PostgreSQL:

**Tabla `cliente`:**

| Columna | Tipo | Descripcion |
|---------|------|-------------|
| `dni_nif` | VARCHAR (PK) | DNI/NIF del cliente |
| `nombre` | VARCHAR | Nombre |
| `apellidos` | VARCHAR | Apellidos |
| `telefono` | VARCHAR | Telefono (9-15 digitos) |
| `email` | VARCHAR | Correo electronico |
| `direccion` | VARCHAR | Direccion |
| `fecha_nac` | DATE | Fecha de nacimiento |
| `sexo` | VARCHAR | Sexo |

**Tabla `coche`:**

| Columna | Tipo | Descripcion |
|---------|------|-------------|
| `matricula` | VARCHAR (PK) | Matricula del vehiculo |
| `num_bastidor` | VARCHAR | Numero de bastidor (17 caracteres) |
| `marca` | VARCHAR | Marca del vehiculo |
| `modelo` | VARCHAR | Modelo |
| `año` | INTEGER | Año de fabricacion |
| `kilometraje` | DOUBLE | Kilometraje actual |
| `disponible_ventas` | BOOLEAN | Disponible para venta |
| `disponible_alquiler` | BOOLEAN | Disponible para alquiler |
| `precio_base` | DOUBLE | Precio base |
| `color` | VARCHAR | Color |
| `diseño_llantas` | VARCHAR | Tipo de llantas |
| `diametro_llantas` | INTEGER | Diametro de llantas |
| `diseño_interior` | VARCHAR | Diseño interior |
| `material_interior` | VARCHAR | Material interior |

**Tabla `transaccion`:**

| Columna | Tipo | Descripcion |
|---------|------|-------------|
| `fecha` | TIMESTAMP | Fecha de inicio |
| `fecha_fin` | TIMESTAMP | Fecha de fin |
| `precio_total` | DOUBLE | Precio total |
| `precio_cuota` | DOUBLE | Precio por cuota |
| `deposito` | DOUBLE | Deposito |
| `estado` | VARCHAR | Estado (PENDIENTE, EN_CURSO, COMPLETADO) |
| `empleado_id` | INTEGER | ID del empleado |
| `hora` | VARCHAR | Hora |
| `id_vehiculo` | VARCHAR | Matricula del vehiculo (FK) |
| `cliente_dni` | VARCHAR | DNI del cliente (FK) |

## Compilacion y Ejecucion

```bash
# Compilar y ejecutar directamente
./gradlew run

# Solo compilar
./gradlew build

# Compilar para generar JAR con dependencias
./gradlew buildForInstaller

# Limpiar y recompilar
./gradlew clean build
```

El JAR generado se encuentra en `app/build/libs/app.jar`.
Las dependencias se copian a `app/build/libs/lib/`.

## Modulos Funcionales

### Inicio
Pantalla principal de bienvenida con navegacion lateral.

### Inventario (Ingresos)
- **Alta de vehiculos:** Registro con todos los datos tecnicos (bastidor, marca, modelo, año, km, precio, color, llantas, interior).
- **Edicion:** Modificacion de vehiculos existentes.
- **Eliminacion:** Borrado de vehiculos del inventario.
- **Listado:** Tabla con todos los vehiculos registrados.
- **Validacion en tiempo real:** Los campos se validan visualmente (rojo/verde) al escribir.
  - Bastidor: exactamente 17 caracteres alfanumericos.
  - Matricula: formato español (4 digitos + 3 letras).
  - Campos numericos: rango valido para año, km, precio, etc.

### Alquileres
- **Registro de alquiler:** Seleccion de cliente (por DNI), vehiculo (por marca/modelo), fechas y horas de recogida/devolucion, precio.
- **Estados:** PENDIENTE, EN_CURSO, COMPLETADO.
- **Gestion de disponibilidad:** Al alquilar, el vehiculo se marca como no disponible; al devolver, se libera.
- **Historial:** Tabla con todas las transacciones completadas.
- **Edicion y cancelacion** de alquileres existentes.

### Compras
- **Busqueda de vehiculos:** Filtros por marca, modelo y color con menus desplegables dependientes.
- **Detalle de vehiculo:** Visualizacion completa de especificaciones.
- **Formulario de financiacion:** Datos bancarios (banco, IBAN, titular), cuota inicial, cuota final y cuota mensual.
- **Generacion de PDF** de informe de compra y ficha del vehiculo.

### Gestion de Clientes
- **CRUD completo** de clientes.
- **Busqueda por DNI.**
- **Validacion de campos:**
  - DNI: algoritmo de verificacion de letra.
  - Edad: entre 18 y 120 años.
  - Email: formato estandar.
  - Telefono: entre 9 y 15 digitos.

## Informes PDF

Los informes se generan en la carpeta `./Informer/` (creada automaticamente junto al ejecutable).

| Informe | Plantilla | Descripcion | Nombre del archivo generado |
|---------|-----------|-------------|-----------------------------|
| Inventario de Coches | `informe_inventario_coches_autoreach.jrxml` | Listado completo del inventario con graficos | Seleccionado por el usuario |
| Informe de Compra | `informe_compra.jrxml` | Factura con datos bancarios y plan de financiacion | `informe_compra_{fecha}.pdf` |
| Detalle de Vehiculo | Generado via HTML (OpenHTMLToPDF) | Ficha tecnica completa del vehiculo | `informe_vehiculo_{matricula}_{fecha}.pdf` |

## Dependencias

```gradle
// Base de datos
implementation 'org.postgresql:postgresql:42.7.3'

// Controles UI y tema
implementation 'org.controlsfx:controlsfx:11.2.1'
implementation 'io.github.mkpaz:atlantafx-base:2.0.1'

// Generacion de informes
implementation 'net.sf.jasperreports:jasperreports:7.0.1'
implementation 'net.sf.jasperreports:jasperreports-pdf:7.0.1'
implementation 'net.sf.jasperreports:jasperreports-charts:7.0.1'

// Conversion HTML a PDF
implementation 'com.openhtmltopdf:openhtmltopdf-core:1.0.10'
implementation 'com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10'
```

**Modulos JavaFX 25:** base, swing, graphics, controls, fxml, media, web.

## Interfaz de Usuario

- **Navegacion lateral** con iconos (sidebar naranja) y 5 secciones: Inicio, Alquiler, Compras, Ingresos, Acerca de.
- **Ventana fija** de 850x600 pixeles.
- **Tema visual:** AtlantaFX NordDark para alertas, esquema de colores naranja personalizado.
- **Tooltips** en los botones de navegacion.
- **Tablas interactivas** (TableView) para visualizar datos.
- **Formularios** con validacion visual en tiempo real.
- **DatePickers** para seleccion de fechas.
- **ComboBoxes dependientes** (marca -> modelo).

## Seguridad

- **Consultas parametrizadas:** Todas las consultas SQL usan `PreparedStatement` para prevenir inyeccion SQL.
- **Validacion de entrada:** Validacion exhaustiva en el lado del cliente para todos los formularios.
- **Rutas relativas:** Todos los recursos se cargan mediante classpath (`getClass().getResource()`), sin rutas absolutas.
- **Codificacion:** UTF-8 con soporte completo para caracteres españoles.

## Notas Importantes

- La aplicacion requiere conexion a internet para acceder a la base de datos en Supabase.
- Los PDFs generados se guardan en la carpeta `Informer/` relativa al directorio de ejecucion.
- La ventana no es redimensionable (tamaño fijo 850x600).
