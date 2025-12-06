# Guía de uso de AlertaEstilizada

## ✨ Características
- **Tema profesional**: Nord Dark (elegante y moderno)
- **Solo las alertas tienen estilo**: Tu aplicación mantiene su aspecto original
- **Fácil de usar**: Métodos simples y concisos

## 📋 Uso

### Antes (Alert tradicional):
```java
Alert alerta = new Alert(AlertType.WARNING);
alerta.setTitle("Título");
alerta.setHeaderText("Encabezado");
alerta.setContentText("Contenido");
alerta.showAndWait();
```

### Después (AlertaEstilizada):
```java
AlertaEstilizada.mostrarAdvertencia("Título", "Encabezado", "Contenido");
```

## 🎯 Métodos disponibles

### 1. Alerta de Información
```java
AlertaEstilizada.mostrarInformacion(
    "Cliente añadido",
    "Éxito",
    "El cliente ha sido creado correctamente"
);
```

### 2. Alerta de Advertencia
```java
AlertaEstilizada.mostrarAdvertencia(
    "Ningún cliente seleccionado",
    "Selecciona un cliente",
    "Por favor, selecciona un cliente de la tabla"
);
```

### 3. Alerta de Error
```java
AlertaEstilizada.mostrarError(
    "Error al eliminar",
    "No se pudo eliminar el cliente",
    "Error: " + e.getMessage()
);
```

### 4. Confirmación (devuelve boolean)
```java
boolean confirmado = AlertaEstilizada.confirmar(
    "Confirmar eliminación",
    "¿Eliminar cliente?",
    "¿Estás seguro de que deseas eliminar a " + nombre + "?"
);

if (confirmado) {
    // Usuario confirmó
}
```

## 🔄 Tabla de conversión rápida

| Código Original | Código con AlertaEstilizada |
|----------------|---------------------------|
| `Alert alerta = new Alert(AlertType.INFORMATION);`<br>`alerta.setTitle(titulo);`<br>`alerta.setHeaderText(header);`<br>`alerta.setContentText(content);`<br>`alerta.showAndWait();` | `AlertaEstilizada.mostrarInformacion(titulo, header, content);` |
| `Alert alerta = new Alert(AlertType.WARNING);`<br>`...`<br>`alerta.showAndWait();` | `AlertaEstilizada.mostrarAdvertencia(titulo, header, content);` |
| `Alert alerta = new Alert(AlertType.ERROR);`<br>`...`<br>`alerta.showAndWait();` | `AlertaEstilizada.mostrarError(titulo, header, content);` |
| `Alert confirmacion = new Alert(AlertType.CONFIRMATION);`<br>`...`<br>`if (confirmacion.showAndWait().get() == ButtonType.OK)` | `if (AlertaEstilizada.confirmar(titulo, header, content))` |

## 🎨 Cambiar tema

Si quieres cambiar el tema profesional, edita `AlertaEstilizada.java` línea 14:

```java
// Temas disponibles:
new NordDark()        // Actual - Profesional y elegante (oscuro)
new NordLight()       // Nord claro
new PrimerDark()      // GitHub oscuro
new PrimerLight()     // GitHub claro
new CupertinoDark()   // macOS oscuro
new CupertinoLight()  // macOS claro
new Dracula()         // Dracula theme
```

## ✅ Ejemplos ya actualizados en tu código:

1. **borrarAlq()** - línea 999
2. **eliminarCliente()** - línea 794

Reemplaza el resto siguiendo el mismo patrón.
