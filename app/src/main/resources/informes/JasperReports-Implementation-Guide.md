# Guía de Implementación Óptima de JasperReports 7.0.1

## Problema detectado

En JasperReports 7.0.1, compilar un `.jrxml` con `JasperCompileManager.compileReport(InputStream)` o `compileReportToFile(String)` lanza:

```
net.sf.jasperreports.engine.JRException: Unable to load report
  at net.sf.jasperreports.engine.xml.JRXmlLoader.loadXML(JRXmlLoader.java:172)
```

Esto ocurre porque el parser XML interno de JR7 tiene problemas con:
- Validación contra XSD (`http://jasperreports.sourceforge.net/xsd/jasperreport.xsd`) cuando no hay conexión a internet
- Dependencias de parser XML faltantes o incompatibles en ciertos entornos
- La diferencia entre carga por `File` vs `InputStream` en ciertos classpaths

## Solución: Construcción programática del reporte (API de diseño)

En lugar de depender de archivos `.jrxml` o `.jasper` precompilados, se construye el `JasperReport` directamente en código Java usando la API de diseño de JasperReports.

Esto **elimina por completo** el parsing XML y la necesidad de archivos externos.

---

## Dependencias necesarias (build.gradle)

```groovy
dependencies {
    implementation 'net.sf.jasperreports:jasperreports:7.0.1'
    implementation 'net.sf.jasperreports:jasperreports-pdf:7.0.1'
    implementation 'net.sf.jasperreports:jasperreports-charts:7.0.1'
}
```

## Imports requeridos

```java
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import java.awt.Color;
```

---

## Arquitectura de JasperReports 7.0.1 (clave para implementar)

A diferencia de versiones anteriores, **JR7 usa un modelo basado en secciones**:

| Componente | Rol | Método clave |
|---|---|---|
| `JasperDesign` | Diseño del reporte (página, márgenes, estilos) | `setTitle(JRBand)`, `setPageFooter(JRBand)`, `getDetailSection()` |
| `JRDesignSection` | Contenedor de bandas de detalle | `addBand(JRBand)` |
| `JRDesignBand` | Banda: una sección vertical del reporte | `setHeight(int)`, `addElement(JRElement)` |
| `JRDesignStaticText` | Texto fijo (etiquetas, títulos) | `setText(String)` |
| `JRDesignTextField` | Texto dinámico (expresiones con parámetros) | `setExpression(JRExpression)` |
| `JRDesignExpression` | Expresión JasperReports (`$P{...}`, `$F{...}`, etc.) | `setText(String)` |
| `JRDesignParameter` | Parámetro de entrada al reporte | `setName(String)`, `setValueClass(Class)` |
| `JRDesignStyle` | Estilo reutilizable (fuente, color, bold) | `setFontSize(Float)`, `setBold(Boolean)`, `setForecolor(Color)` |
| `JRDesignRectangle` | Rectángulo (fondos, cards) | `setX()`, `setY()`, `setWidth()`, `setHeight()`, `setBackcolor(Color)` |
| `JRDesignLine` | Línea horizontal/vertical | `setX()`, `setY()`, `setWidth()`, `setHeight()`, `setForecolor(Color)` |

### Flujo de construcción

```
JasperDesign
  ├── setTitle(JRDesignBand)
  ├── getDetailSection() → JRDesignSection → addBand(JRDesignBand)
  ├── setPageFooter(JRDesignBand)
  ├── addParameter(JRDesignParameter)
  └── addStyle(JRDesignStyle)
        │
        ▼
JasperCompileManager.compileReport(JasperDesign) → JasperReport
        │
        ▼
JasperFillManager.fillReport(JasperReport, params, dataSource) → JasperPrint
```

---

## Método principal: construirReporte()

```java
private JasperReport construirReporte() throws JRException {
    // 1. Configurar diseño base
    JasperDesign design = new JasperDesign();
    design.setName("nombre_reporte");
    design.setPageWidth(595);     // A4
    design.setPageHeight(842);
    design.setColumnWidth(535);
    design.setLeftMargin(30);
    design.setRightMargin(30);
    design.setTopMargin(30);
    design.setBottomMargin(30);
    design.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

    // 2. Parámetros
    addParam(design, "FECHA", String.class);
    // ... más parámetros

    // 3. Estilos
    JRDesignStyle th = estilo("TH", 24, true, Color.WHITE);
    // ... más estilos
    for (JRDesignStyle s : new JRDesignStyle[]{th, ...})
        design.addStyle(s);

    // 4. Title band
    JRDesignBand titleBand = new JRDesignBand();
    titleBand.setHeight(100);
    // ... elementos
    design.setTitle(titleBand);

    // 5. Detail band (secuencia repetida por cada fila de datos)
    JRDesignBand detailBand = new JRDesignBand();
    detailBand.setHeight(380);
    // ... elementos
    ((JRDesignSection) design.getDetailSection()).addBand(detailBand);

    // 6. Footer
    JRDesignBand footerBand = new JRDesignBand();
    footerBand.setHeight(40);
    // ... elementos
    design.setPageFooter(footerBand);

    // 7. Compilar
    return JasperCompileManager.compileReport(design);
}
```

---

## Helpers compactos (patrón recomendado)

```java
// Añadir parámetro al diseño
private void addParam(JasperDesign d, String n, Class<?> c) throws JRException {
    JRDesignParameter p = new JRDesignParameter();
    p.setName(n);
    p.setValueClass(c);
    d.addParameter(p);
}

// Crear estilo reutilizable
private JRDesignStyle estilo(String n, int f, boolean b, Color c) {
    JRDesignStyle s = new JRDesignStyle();
    s.setName(n);
    s.setFontSize((float) f);
    s.setBold(b);
    s.setForecolor(c);
    return s;
}

// Rectángulo con color de fondo
private void rect(JRDesignBand b, int x, int y, int w, int h, Color bg) {
    JRDesignRectangle r = new JRDesignRectangle();
    r.setX(x); r.setY(y); r.setWidth(w); r.setHeight(h);
    r.setBackcolor(bg);
    b.addElement(r);
}

// Texto estático (etiqueta o título)
private JRDesignStaticText stxt(JRDesignBand b, JRDesignStyle s, String t, int x, int y, int w, int h) {
    JRDesignStaticText st = new JRDesignStaticText();
    st.setStyle(s);
    st.setX(x); st.setY(y); st.setWidth(w); st.setHeight(h);
    st.setText(t);
    b.addElement(st);
    return st;
}

// Campo de texto dinámico con expresión
private void txt(JRDesignBand b, JRDesignStyle s, String expr, int x, int y, int w, int h) {
    JRDesignTextField tf = new JRDesignTextField();
    tf.setStyle(s);
    tf.setX(x); tf.setY(y); tf.setWidth(w); tf.setHeight(h);
    JRDesignExpression e = new JRDesignExpression();
    e.setText(expr);
    tf.setExpression(e);
    b.addElement(tf);
}

// Línea decorativa
private void linea(JRDesignBand b, int x, int y, int w, Color c) {
    JRDesignLine l = new JRDesignLine();
    l.setX(x); l.setY(y); l.setWidth(w); l.setHeight(1);
    l.setForecolor(c);
    b.addElement(l);
}
```

### Helpers de alto nivel (campo con null-check)

```java
// Etiqueta tamaño estándar (100x20)
private void etiqueta(JRDesignBand b, JRDesignStyle s, String t, int x, int y) {
    stxt(b, s, t, x, y, 100, 20);
}

// Valor con null-check automático (400x20)
// Genera: $P{NOMBRE} != null && !$P{NOMBRE}.isEmpty() ? $P{NOMBRE} : "-"
private void valor(JRDesignBand b, JRDesignStyle s, String expr, int x, int y) {
    txt(b, s, expr + " != null && !" + expr + ".isEmpty() ? " + expr + " : \"-\"", x, y, 400, 20);
}
```

---

## Uso en vista previa HTML y exportación PDF

### Vista previa en WebView (JavaFX)

```java
JasperReport report = construirReporte();

Map<String, Object> params = new HashMap<>();
params.put("FECHA", LocalDate.now().toString());
// ... llenar parámetros

JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

// Exportar a HTML
ByteArrayOutputStream baos = new ByteArrayOutputStream();
HtmlExporter exporter = new HtmlExporter();
exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
exporter.setExporterOutput(new SimpleHtmlExporterOutput(baos));
exporter.exportReport();

// Mostrar en WebView
String htmlContent = baos.toString("UTF-8");
WebView webView = new WebView();
webView.getEngine().loadContent(htmlContent);
```

### Exportación directa a PDF

```java
JasperReport report = construirReporte();
JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

File archivo = new File(rutaCarpeta, "informe.pdf");
JasperExportManager.exportReportToPdfFile(jasperPrint, archivo.getAbsolutePath());
```

---

## Ventajas de este enfoque

| Aspecto | `.jrxml` / `.jasper` | Programático |
|---|---|---|
| **Compilación** | Falla si el parser XML no resuelve XSD | No hay XML, compila siempre |
| **Dependencias externas** | Requiere archivo `.jrxml` en resources | Todo está en el código fuente |
| **Mantenimiento** | Editar XML a mano o con Jaspersoft Studio | Refactorización Java directa |
| **Portabilidad** | El `.jrxml` no es portable entre versiones de JR | La API de diseño es estable |
| **Depuración** | "Unable to load report" sin más detalle | Stack trace Java completo |

---

## Nota importante

Si necesitas **cargar un `.jasper` precompilado** (proveniente de Jaspersoft Studio), usa:

```java
InputStream reportStream = getClass().getResourceAsStream("/informes/mi_reporte.jasper");
JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, dataSource);
```

Pero si el `.jasper` no existe o no puedes compilar el `.jrxml` en tu entorno, la construcción programática es la solución más robusta.
