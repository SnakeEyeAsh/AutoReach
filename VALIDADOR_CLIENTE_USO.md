# Guía de Uso: ValidadorCliente

## Resumen de mejoras

La clase `ValidadorCliente` ha sido optimizada con las siguientes mejoras:

### ✅ Mejoras implementadas:

1. **Protección robusta contra campos vacíos**: Todos los campos verifican `null`, vacío y espacios en blanco
2. **Validación de DNI mejorada**: Ahora valida la letra correcta del DNI español
3. **Validación de edad**: Verifica que el cliente sea mayor de 18 años
4. **Validación de fechas**: Solo acepta formato `dd/MM/yyyy` o `yyyy-MM-dd`
5. **Mejor validación de teléfono**: Acepta 9-15 dígitos con o sin `+`
6. **Validación de nombres**: Solo permite letras (incluye acentos y ñ)
7. **Validador único**: Uso más eficiente de memoria (antes: 8 validadores, ahora: 1)
8. **API más clara**: Métodos con nombres descriptivos

---

## Implementación en el Controlador

### 1. Inicialización (ya está implementado)

En el método `initialize()` de tu controlador:

```java
validador = new ValidadorCliente();
validador.inicializarValidadores(
    txtNombre,
    txtApellidos,
    txtDireccion,
    txtTelefono,
    txtFechaNac,
    txtDni,
    txtEmail,
    splitMenuSexo
);
```

### 2. Validar antes de guardar (ya está implementado)

En el método `añadirCliente()`:

```java
@FXML
void añadirCliente(ActionEvent event) {
    // Opción 1: Validar y mostrar errores automáticamente (RECOMENDADO)
    if (validador.validarYMostrarErrores()) {
        // Código para guardar el cliente
        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        // ... resto del código
    }
}
```

### 3. Validación silenciosa (sin alertas)

Si solo quieres verificar sin mostrar alertas:

```java
// Opción 2: Validar sin mostrar alertas
if (validador.esFormularioValido()) {
    // El formulario es válido
} else {
    // El formulario tiene errores
    // Mostrar tu propia alerta personalizada
}
```

### 4. Obtener lista de errores

Para manejar los errores manualmente:

```java
// Opción 3: Obtener lista de errores
List<String> errores = validador.obtenerErrores();
if (errores.isEmpty()) {
    // Todo correcto
} else {
    // Procesar los errores a tu manera
    for (String error : errores) {
        System.out.println("Error: " + error);
    }
}
```

---

## Métodos Disponibles

### `inicializarValidadores(...)`
Inicializa el validador con todos los campos. **Llamar solo una vez** en `initialize()`.

### `validarYMostrarErrores()`
- Valida todos los campos
- Muestra una alerta con los errores encontrados
- Retorna `true` si es válido, `false` si hay errores
- **Recomendado para usar antes de guardar**

### `esFormularioValido()`
- Valida todos los campos
- NO muestra alertas
- Retorna `true` si es válido, `false` si hay errores
- **Útil para validaciones silenciosas**

### `obtenerErrores()`
- Retorna una lista de strings con los mensajes de error
- NO muestra alertas
- **Útil para manejo personalizado de errores**

### `limpiar()`
- Libera recursos del validador
- **Llamar cuando se destruya el formulario** (opcional)

### `getValidador()`
- Retorna el `ValidationSupport` interno
- **Solo para uso avanzado**

---

## Reglas de Validación

### Nombre
- ✅ Obligatorio
- ✅ Mínimo 2 caracteres
- ✅ Máximo 50 caracteres
- ✅ Solo letras (incluye áéíóúñ)

### Apellidos
- ✅ Obligatorio
- ✅ Mínimo 2 caracteres
- ✅ Máximo 100 caracteres
- ✅ Solo letras (incluye áéíóúñ)

### Dirección
- ✅ Obligatoria
- ✅ Mínimo 5 caracteres
- ✅ Máximo 200 caracteres
- ✅ Acepta números y caracteres especiales

### Teléfono
- ✅ Obligatorio
- ✅ 9-15 dígitos
- ✅ Puede incluir `+` al inicio
- ✅ Acepta espacios y guiones (se eliminan automáticamente)
- ✅ Ejemplos válidos: `612345678`, `+34 612 345 678`, `+34-612-345-678`

### Email
- ✅ Obligatorio
- ✅ Formato válido: `usuario@dominio.com`
- ✅ Mínimo 2 caracteres en el dominio (ej: `.es`, `.com`)

### DNI
- ✅ Obligatorio
- ✅ Formato: 8 dígitos + letra (ej: `12345678Z`)
- ✅ **Valida que la letra sea correcta**
- ✅ Convierte automáticamente a mayúsculas

### Fecha de Nacimiento
- ✅ Obligatoria
- ✅ Formato: `dd/MM/yyyy` o `yyyy-MM-dd`
- ✅ No puede ser futura
- ✅ **Edad mínima: 18 años**
- ✅ Edad máxima: 120 años

### Sexo
- ✅ Obligatorio
- ✅ Debe seleccionar una opción del menú
- ✅ No acepta valores por defecto como "Sexo" o "Seleccione"

---

## Ejemplos de Uso Avanzado

### Validar campo por campo

```java
// Validar y obtener errores específicos
List<String> errores = validador.obtenerErrores();
for (String error : errores) {
    if (error.contains("DNI")) {
        // Hacer algo específico para DNI
        txtDni.setStyle("-fx-border-color: red;");
    }
}
```

### Habilitar/deshabilitar botón según validez

```java
// En el initialize() o donde configures los listeners
txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
    btnAñadirNuevoCliente.setDisable(!validador.esFormularioValido());
});
// Repetir para cada campo...
```

### Validación antes de cambiar de pestaña

```java
@FXML
void cambiarAPestañaAlquiler(ActionEvent event) {
    if (!validador.esFormularioValido()) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Formulario incompleto");
        alerta.setContentText("Completa el formulario antes de continuar");
        alerta.showAndWait();
        return;
    }
    // Cambiar de pestaña
}
```

---

## Ventajas vs. Versión Anterior

| Característica | Antes | Ahora |
|----------------|-------|-------|
| Validadores | 8 objetos | 1 objeto |
| Memoria | ~8x más | Optimizado |
| Campos vacíos | Validación básica | Protección robusta |
| DNI | Solo formato | Valida letra correcta |
| Edad | No validaba | Mínimo 18 años |
| API | `comprobarValidaciones()` | 3 métodos claros |
| Errores | Solo alerta | Alerta u obtener lista |
| Mantenimiento | Código repetitivo | Métodos privados reutilizables |

---

## Solución de Problemas

### El validador no muestra decoraciones visuales
- Verifica que hayas llamado `inicializarValidadores()` en el `initialize()`
- Asegúrate de que los campos estén correctamente cargados desde el FXML

### Las validaciones no funcionan
- Verifica que el validador esté inicializado antes de usar
- Comprueba que los campos no sean `null`

### Quiero cambiar las reglas de validación
- Modifica los métodos privados `registrarValidador*()` en [ValidadorCliente.java](app/src/main/java/com/javafx/Model/ValidadorCliente.java)

---

## Próximas mejoras posibles

Si necesitas extender el validador:

1. **Validación de NIE**: Modificar `registrarValidadorDni()` para aceptar NIE
2. **Campos opcionales**: Crear versión del validador que acepte campos opcionales
3. **Validación personalizada**: Añadir método para registrar validadores custom
4. **Validación en tiempo real**: Añadir listeners para validar mientras escribes

---

**Documentación actualizada**: 2025-12-02
**Versión del validador**: 2.0 (Optimizada)
