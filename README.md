# AutoReach - Sistema de Gestion de Vehiculos

Aplicacion JavaFX para la gestion integral de vehiculos: inventario, alquileres y compras.

## Tecnologias

- **JavaFX 25** - Interfaz grafica
- **PostgreSQL** (Supabase) - Base de datos
- **JasperReports 7.0.1** - Generacion de informes PDF
- **OpenHTMLToPDF 1.0.10** - Conversion HTML a PDF
- **Gradle** - Gestion de dependencias

## Estructura del Proyecto

```
AppInterfaces/
├── app/
│   └── src/main/
│       ├── java/com/javafx/
│       │   ├── App/           # Controladores
│       │   ├── Model/         # Modelos de datos
│       │   ├── JDBC/          # Conexion BD
│       │   └── Data/          # Gestion datos
│       └── resources/
│           ├── Assets/        # FXML e imagenes
│           └── informes/      # Plantillas Jasper
├── Informer/                  # Plantillas HTML de informes
└── docs/                      # Documentacion
```

## Modulos

### Inventario
- CRUD completo de vehiculos
- Busqueda y filtrado
- Exportacion de reportes PDF

### Alquileres
- Registro de transacciones
- Gestion de clientes
- Historial de alquileres

### Compras
- Formulario de financiacion
- Generacion de informe de compra en PDF
- Datos bancarios y cuotas

## Informes Disponibles

| Informe | Formato | Descripcion |
|---------|---------|-------------|
| Inventario de Coches | Jasper/PDF | Listado completo con graficos estadisticos |
| Informe de Compra | Jasper/PDF | Datos bancarios y financiacion |
| Detalle de Vehiculo | Jasper/PDF | Ficha tecnica del vehiculo seleccionado |

Ver [INFORMES.md](INFORMES.md) para documentacion detallada.

## Actualizaciones Recientes

### v1.2.0 - Informe de Vehiculo (Enero 2026)

**Nuevas funcionalidades:**
- Boton "Informe Vehiculo" en el panel de compras principal (`pantalla1.fxml`)
- El boton se activa solo cuando hay un vehiculo seleccionado en la tabla
- Genera PDF con ficha tecnica completa del vehiculo
- Validacion con alerta estilizada si no hay vehiculo seleccionado

**Archivos modificados:**
- `controladorApp.java` - Nuevo metodo `descargarInformeVehiculo()` y logica de activacion del boton
- `pantalla1.fxml` - Añadido boton "Informe Vehiculo" en panelVentas

**Archivos creados:**
- `informes/informe_detalle_vehiculo.jrxml` - Plantilla Jasper del informe
- `Informer/informe_detalle_vehiculo.html` - Version HTML de referencia

---

### v1.1.0 - Modulo de Compra (Enero 2026)

**Nuevas funcionalidades:**
- Boton "Descargar PDF" en el modulo de compra
- Generacion de informes usando JasperReports
- Plantilla `informe_compra.jrxml` con diseño corporativo

**Archivos modificados:**
- `controladorCompra.java` - Implementacion de descarga PDF con Jasper
- `pantallaCompra.fxml` - Añadido boton de descarga

**Archivos creados:**
- `informes/informe_compra.jrxml` - Plantilla Jasper del informe
- `Informer/informe_compra.html` - Version HTML de referencia
- `Informer/informe_inventario_coches_autoreach.html` - Version HTML de referencia

**Limpieza:**
- Eliminados archivos Jasper no utilizados (`informe_coches_basico.*`)
- Reorganizada carpeta Informer con HTMLs correspondientes a cada Jasper

## Ejecucion

```bash
# Compilar y ejecutar
./gradlew run

# Solo compilar
./gradlew build
```

## Configuracion

La configuracion de base de datos se encuentra en:
```
app/src/main/resources/Application.properties
```

## Dependencias Principales

```gradle
// JavaFX
implementation 'org.openjfx:javafx-controls:25'
implementation 'org.openjfx:javafx-fxml:25'
implementation 'org.openjfx:javafx-web:25'

// JasperReports
implementation 'net.sf.jasperreports:jasperreports:7.0.1'
implementation 'net.sf.jasperreports:jasperreports-pdf:7.0.1'

// OpenHTMLToPDF
implementation 'com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10'

// PostgreSQL
implementation 'org.postgresql:postgresql:42.7.2'
```
