# Sistema de Informes - AutoReach

Documentacion tecnica del sistema de generacion de informes PDF.

## Arquitectura

La aplicacion utiliza **JasperReports 7.0.1** para la generacion de informes PDF. Las plantillas se definen en archivos `.jrxml` que se compilan a `.jasper` para su uso en produccion.

```
resources/informes/
├── informe_inventario_coches_autoreach.jrxml   # Plantilla fuente
├── informe_inventario_coches_autoreach.jasper  # Compilado
├── informe_compra.jrxml                        # Plantilla fuente
├── informe_compra.jasper                       # Compilado
├── informe_detalle_vehiculo.jrxml              # Plantilla fuente
└── informe_detalle_vehiculo.jasper             # Compilado

Informer/
├── informe_inventario_coches_autoreach.html    # Version HTML referencia
├── informe_compra.html                         # Version HTML referencia
└── informe_detalle_vehiculo.html               # Version HTML referencia
```

## Informes Disponibles

### 1. Informe de Detalle de Vehiculo

**Archivo:** `informe_detalle_vehiculo.jrxml`

**Descripcion:** Ficha individual con todos los datos de un vehiculo seleccionado.

**Usado en:**
- `controladorApp.java:1221` - Panel de compras principal (boton deshabilitado hasta seleccionar vehiculo)
- `controladorCompra.java:127` - Ventana modal de compra

```java
InputStream reportStream = getClass().getResourceAsStream("/informes/informe_detalle_vehiculo.jrxml");
```

**Activacion del boton:** El boton "Informe Vehiculo" en `pantalla1.fxml` esta deshabilitado por defecto y se activa automaticamente cuando el usuario selecciona un vehiculo en la tabla.

**Parametros:**
| Parametro | Tipo | Descripcion |
|-----------|------|-------------|
| `FECHA` | String | Fecha del informe |
| `MATRICULA` | String | Matricula del vehiculo |
| `MARCA` | String | Marca del vehiculo |
| `MODELO` | String | Modelo del vehiculo |
| `ANO` | String | Año de fabricacion |
| `COLOR` | String | Color del vehiculo |
| `PRECIO` | String | Precio de venta |
| `DIAMETRO_LLANTAS` | String | Diametro de las llantas |
| `DISENO_RUEDAS` | String | Diseño de las ruedas |
| `INTERIOR` | String | Tipo de interior |
| `MATERIAL` | String | Material del interior |

**Secciones del informe:**
1. **Header** - Matricula destacada, titulo
2. **Datos Generales** - Marca, modelo, año, color
3. **Especificaciones** - Llantas, ruedas, interior, material
4. **Precio** - Precio de venta destacado
5. **Footer** - AutoReach

**Validacion:** Si no hay vehiculo seleccionado, muestra alerta estilizada.

---

### 2. Informe de Inventario de Coches

**Archivo:** `informe_inventario_coches_autoreach.jrxml`

**Descripcion:** Listado completo de vehiculos en inventario con estadisticas y graficos.

**Usado en:** `controladorApp.java:1318`

```java
InputStream reportStream = getClass().getResourceAsStream("/informes/informe_inventario_coches_autoreach.jasper");
```

**Campos de datos (fields):**
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `num_bastidor` | String | Numero de bastidor |
| `marca` | String | Marca del vehiculo |
| `modelo` | String | Modelo del vehiculo |
| `año` | Integer | Año de fabricacion |
| `matricula` | String | Matricula |

**Parametros:**
| Parametro | Tipo | Valor por defecto |
|-----------|------|-------------------|
| `REPORT_TITLE` | String | "INVENTARIO DE VEHICULOS" |
| `REPORT_SUBTITLE` | String | "Catalogo Completo de Coches" |

**Secciones del informe:**
1. **Header** - Logo AutoReach, titulo, fecha de generacion
2. **Tabla** - Listado de vehiculos con filas alternadas
3. **Estadisticas** - Total de vehiculos
4. **Graficos** - Distribucion por marca y por año
5. **Footer** - Datos de contacto y numero de pagina

---

### 3. Informe de Compra

**Archivo:** `informe_compra.jrxml`

**Descripcion:** Documento de compra con datos bancarios y financiacion.

**Usado en:** `controladorCompra.java:106`

```java
InputStream reportStream = getClass().getResourceAsStream("/informes/informe_compra.jrxml");
JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
```

**Parametros:**
| Parametro | Tipo | Descripcion |
|-----------|------|-------------|
| `FECHA` | String | Fecha del informe |
| `BANCO` | String | Nombre del banco |
| `IBAN` | String | Numero IBAN |
| `TITULAR` | String | Nombre del titular |
| `CUOTA_INICIAL` | String | Cuota inicial en EUR |
| `CUOTA_FINAL` | String | Cuota final en EUR |
| `CUOTA_MENSUAL` | String | Cuota mensual en EUR |

**Secciones del informe:**
1. **Header** - Titulo y fecha
2. **Datos Bancarios** - Banco, IBAN, Titular
3. **Financiacion** - Cuotas inicial, final y mensual
4. **Footer** - Texto legal

---

## Estilos Corporativos

Los informes utilizan la paleta de colores de AutoReach:

| Color | Hex | Uso |
|-------|-----|-----|
| Azul Primario | `#1E3A8A` | Headers, titulos |
| Azul Secundario | `#3B82F6` | Acentos |
| Naranja | `#F97316` | Destacados, barras |
| Gris Texto | `#374151` | Texto principal |
| Gris Claro | `#6B7280` | Texto secundario |
| Fondo Claro | `#F0F9FF` | Filas pares |
| Borde | `#DBEAFE` | Lineas separadoras |

---

## Compilacion de Plantillas

### Opcion 1: JasperSoft Studio (Recomendado)

1. Abrir el archivo `.jrxml` en JasperSoft Studio
2. Click en "Compile Report" o Ctrl+F9
3. Se genera el archivo `.jasper` en la misma ubicacion

### Opcion 2: Compilacion en tiempo de ejecucion

```java
InputStream reportStream = getClass().getResourceAsStream("/informes/informe_compra.jrxml");
JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
```

### Opcion 3: Compilacion programatica

```java
JasperCompileManager.compileReportToFile(
    "ruta/informe.jrxml",
    "ruta/informe.jasper"
);
```

---

## Generacion de PDF

### Usando archivo .jasper precompilado

```java
// Cargar el .jasper
InputStream reportStream = getClass().getResourceAsStream("/informes/informe.jasper");

// Preparar parametros
Map<String, Object> parametros = new HashMap<>();
parametros.put("PARAM1", "valor1");

// Llenar con datos
JasperPrint jasperPrint = JasperFillManager.fillReport(
    reportStream,
    parametros,
    dataSource  // JRBeanCollectionDataSource o JREmptyDataSource
);

// Exportar a PDF
JasperExportManager.exportReportToPdfFile(jasperPrint, "ruta/salida.pdf");
```

### Usando archivo .jrxml (compila en tiempo de ejecucion)

```java
// Cargar y compilar el .jrxml
InputStream reportStream = getClass().getResourceAsStream("/informes/informe.jrxml");
JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

// Llenar con datos
JasperPrint jasperPrint = JasperFillManager.fillReport(
    jasperReport,
    parametros,
    new JREmptyDataSource()  // Sin datos, solo parametros
);

// Exportar a PDF
JasperExportManager.exportReportToPdfFile(jasperPrint, "ruta/salida.pdf");
```

---

## Plantillas HTML de Referencia

La carpeta `Informer/` contiene versiones HTML de cada informe Jasper. Estas plantillas sirven como:

- Referencia visual del diseño
- Documentacion del layout
- Base para modificaciones rapidas

Los placeholders usan formato `{{PARAMETRO}}` para indicar donde van los datos dinamicos.

---

## Dependencias

```gradle
// JasperReports Core
implementation 'net.sf.jasperreports:jasperreports:7.0.1'

// Exportacion a PDF
implementation 'net.sf.jasperreports:jasperreports-pdf:7.0.1'

// Graficos
implementation 'net.sf.jasperreports:jasperreports-charts:7.0.1'
```

---

## Troubleshooting

### Error: "JREmptyDataSource cannot be resolved"
El import correcto en JasperReports 7.x es:
```java
import net.sf.jasperreports.engine.JREmptyDataSource;
```

### Error al abrir .jrxml en JasperSoft Studio
Verificar que:
1. Los elementos `<style>` van ANTES de `<parameter>`
2. Los UUIDs tienen formato valido: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
3. El orden de elementos sigue el XSD de JasperReports

### El PDF no muestra colores
Añadir en los estilos CSS del HTML o en las propiedades del informe:
```css
@media print {
    body { -webkit-print-color-adjust: exact !important; }
}
```
