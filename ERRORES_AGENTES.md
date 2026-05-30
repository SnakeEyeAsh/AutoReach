# Errores del módulo Agentes — AutoReach

**Proyecto:** AutoReach — app de escritorio JavaFX para gestión de alquiler/venta de vehículos  
**Stack:** JavaFX 21, JDK 17, Gradle 8, comunicación con API Node.js/Express vía `java.net.http.HttpClient` + Gson  
**Autenticación de agentes:** bcrypt + JWT propio del backend (NO Supabase Auth)  
**Compilación actual:** BUILD SUCCESSFUL — todos los errores son funcionales/lógicos, no de compilación

---

## Archivos que hay que modificar

| Archivo | Ruta completa |
|---------|---------------|
| `controladorApp.java` | `app/src/main/java/com/javafx/App/controladorApp.java` |
| `Conexion.java` | `app/src/main/java/com/javafx/JDBC/Conexion.java` |
| `ValidadorAgente.java` | `app/src/main/java/com/javafx/Model/ValidadorAgente.java` |

---

## Error 1 — `editarAgente()` en el controlador no deja al usuario editar nada antes de guardar

**Archivo:** `controladorApp.java`  
**Severidad:** Alta — la edición de agentes no funciona  
**Estado: ✅ REPARADO** — `editarAgente()` ya solo carga datos en el formulario y muestra mensaje informativo. El campo `agenteEnEdicion` existe. `cancelarNuevoAgente()` ya resetea el estado.

### Problema
El método `editarAgente()` carga datos en el formulario y acto seguido lanza el diálogo de confirmación "¿Guardar cambios?" de forma bloqueante (`showAndWait`). El usuario no puede tocar ningún campo antes de que aparezca el diálogo. Si confirma, se guardan los mismos valores que ya había en la BD.

Además, el método en `Conexion.java` nunca envía la contraseña al editar (ver Error 4), así que aunque se arregle el flujo de UI, el cambio de contraseña seguirá sin funcionar hasta corregir también `Conexion.editarAgente()`.

### Código actual (roto)
```java
@FXML
void editarAgente(ActionEvent event) {
    Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
    if (seleccionado == null) { ... return; }

    // 1. Rellena el formulario
    txtAgenteUsername.setText(seleccionado.getUsername());
    txtAgenteNombre.setText(seleccionado.getNombre());
    txtAgenteApellidos.setText(seleccionado.getApellidos());
    txtAgenteTelefono.setText(seleccionado.getTelefono());
    txtAgenteEmail.setText(seleccionado.getEmail());
    pfAgentePassword.clear();
    pfAgentePassword.setPromptText("Dejar vacío para no cambiar");

    // 2. Inmediatamente pregunta si guardar — el usuario no puede modificar nada
    boolean confirmado = AlertaEstilizada.confirmar("Editar Agente", "¿Guardar cambios?",
            "Se actualizarán los datos del agente " + seleccionado.getUsername());
    if (!confirmado) return;

    // 3. Lee el formulario sin cambios y guarda
    seleccionado.setUsername(txtAgenteUsername.getText().trim());
    // ...
    Conexion.editarAgente(seleccionado);
}
```

### Solución

**Paso 1:** Añadir un campo de estado junto a los otros campos privados del controlador (buscar la zona donde están `private ValidadorAgente validadorAgente;`):

```java
private Agente agenteEnEdicion = null;
```

**Paso 2:** Reemplazar `editarAgente()` completo para que SOLO cargue datos y guarde la referencia, sin diálogo ni guardado:

```java
@FXML
void editarAgente(ActionEvent event) {
    Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
    if (seleccionado == null) {
        AlertaEstilizada.mostrarAdvertencia("Sin selección",
                "Ningún agente seleccionado", "Selecciona un agente de la tabla para editar");
        return;
    }

    agenteEnEdicion = seleccionado;
    txtAgenteUsername.setText(seleccionado.getUsername());
    txtAgenteNombre.setText(seleccionado.getNombre());
    txtAgenteApellidos.setText(seleccionado.getApellidos());
    txtAgenteTelefono.setText(seleccionado.getTelefono());
    txtAgenteEmail.setText(seleccionado.getEmail());
    pfAgentePassword.clear();
    pfAgentePassword.setPromptText("Dejar vacío para no cambiar");

    AlertaEstilizada.mostrarInformacion("Modo edición",
            "Editando agente",
            "Modifica los campos y pulsa 'Guardar' para aplicar los cambios.");
}
```

---

## Error 2 — `guardarAgente()` no distingue entre crear y editar, y no valida teléfono/email

**Archivo:** `controladorApp.java`  
**Severidad:** Alta  
**Estado: ✅ REPARADO PARCIALMENTE** — El controlador ya distingue crear/editar con `agenteEnEdicion`, ya valida con `validadorAgente.isValid()`, y la contraseña solo es obligatoria al crear. **PERO**: en la línea donde llama a `Conexion.editarAgente()` todavía usa la firma antigua sin password (`Conexion.editarAgente(agenteEnEdicion)`). Quedará completamente reparado cuando se aplique el Error 4.

### Problema
- Solo crea agentes nuevos, nunca actualiza.
- La validación manual solo comprueba que `username`, `password`, `nombre` y `apellidos` no estén vacíos. Nunca llama a `validadorAgente.isValid()`, así que el regex de teléfono (9–15 dígitos) y el regex de email nunca se aplican al guardar.

### Código actual (roto)
```java
@FXML
void guardarAgente(ActionEvent event) {
    // ...
    if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
        AlertaEstilizada.mostrarAdvertencia(...);
        return;
    }
    if (!password.matches(".{6,}")) { ... return; }
    // No distingue crear de editar
    // No llama a validadorAgente.isValid()
    Conexion.crearAgente(username, password, nombre, apellidos, telefono, email);
}
```

### Solución — Reemplazar `guardarAgente()` y `cancelarNuevoAgente()` completos

```java
@FXML
void guardarAgente(ActionEvent event) {
    String username  = txtAgenteUsername.getText().trim();
    String password  = pfAgentePassword.getText();
    String nombre    = txtAgenteNombre.getText().trim();
    String apellidos = txtAgenteApellidos.getText().trim();
    String telefono  = txtAgenteTelefono.getText().trim();
    String email     = txtAgenteEmail.getText().trim();

    if (username.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
        AlertaEstilizada.mostrarAdvertencia("Campos incompletos",
                "Datos requeridos", "Usuario, nombre y apellidos son obligatorios");
        return;
    }

    // Contraseña obligatoria solo al crear
    if (agenteEnEdicion == null && password.isEmpty()) {
        AlertaEstilizada.mostrarAdvertencia("Contraseña requerida",
                "Dato requerido", "La contraseña es obligatoria al crear un agente");
        return;
    }

    if (!password.isEmpty() && !password.matches(".{6,}")) {
        AlertaEstilizada.mostrarAdvertencia("Contraseña débil",
                "Longitud mínima", "La contraseña debe tener al menos 6 caracteres");
        return;
    }

    // Validar formato de teléfono y email con ControlsFX
    if (!validadorAgente.isValid()) {
        AlertaEstilizada.mostrarAdvertencia("Datos inválidos",
                "Revisa el formulario", "El teléfono o el email no tienen el formato correcto");
        return;
    }

    if (agenteEnEdicion != null) {
        // Modo edición
        agenteEnEdicion.setUsername(username);
        agenteEnEdicion.setNombre(nombre);
        agenteEnEdicion.setApellidos(apellidos);
        agenteEnEdicion.setTelefono(telefono);
        agenteEnEdicion.setEmail(email);
        try {
            Conexion.editarAgente(agenteEnEdicion, password.isEmpty() ? null : password);
            AlertaEstilizada.mostrarInformacion("Agente actualizado", "Éxito",
                    "Los datos del agente han sido actualizados");
            agenteEnEdicion = null;
            limpiarFormularioAgente();
            refrescarTablaAgentes();
        } catch (Exception e) {
            AlertaEstilizada.mostrarError("Error al actualizar agente",
                    "No se pudo actualizar", e.getMessage());
        }
    } else {
        // Modo creación
        try {
            Conexion.crearAgente(username, password, nombre, apellidos, telefono, email);
            AlertaEstilizada.mostrarInformacion("Agente creado", "Éxito",
                    "El agente " + username + " ha sido creado correctamente");
            limpiarFormularioAgente();
            refrescarTablaAgentes();
        } catch (Exception e) {
            AlertaEstilizada.mostrarError("Error al crear agente",
                    "No se pudo crear", e.getMessage());
        }
    }
}

@FXML
void cancelarNuevoAgente(ActionEvent event) {
    agenteEnEdicion = null;
    limpiarFormularioAgente();
}
```

> **Nota:** `Conexion.editarAgente()` ahora recibe un segundo parámetro `String password` (puede ser `null`). Ver Error 4 para el cambio en `Conexion.java`.

---

## Error 3 — `ValidadorAgente` no tiene el método `validarYMostrarErrores()`

**Archivo:** `ValidadorAgente.java`  
**Severidad:** Media  
**Estado: ✅ REPARADO** — El método `validarYMostrarErrores()` ya existe en el archivo.

### Problema
Todos los demás validadores del proyecto (`ValidadorCliente`, `ValidadorCoche`, `ValidadorAlquiler`) tienen el método `validarYMostrarErrores()`. `ValidadorAgente` solo tiene `isValid()`. Rompe el patrón uniforme del código.

### Código actual
```java
public boolean isValid() {
    if (validador == null) return false;
    ValidationResult resultado = validador.getValidationResult();
    return resultado.getErrors().isEmpty();
}
// No existe validarYMostrarErrores()
```

### Solución — Añadir el método antes del cierre `}` de la clase
```java
public boolean validarYMostrarErrores() {
    if (validador == null) return false;
    return isValid();
}
```

---

## Error 4 — `Conexion.editarAgente()` nunca envía la contraseña al backend

**Archivo:** `Conexion.java` líneas 1281–1299 y `controladorApp.java` línea 2669  
**Severidad:** Alta — cambiar contraseña de un agente es imposible desde la app  
**Estado: ✅ REPARADO** — `Conexion.editarAgente()` ahora acepta `(Agente a, String nuevaPassword)`. El controlador pasa la contraseña del formulario.

### Problema
El cuerpo del `PUT /api/agentes/{id}` incluye `username`, `nombre`, `apellidos`, `telefono`, `email`, `activo`, pero **nunca incluye `password`**. El formulario muestra `pfAgentePassword` con prompt "Dejar vacío para no cambiar", dando a entender que se puede cambiar la contraseña, pero esa contraseña se ignora completamente.

### Código actual (roto)
```java
public static void editarAgente(Agente a) {
    JsonObject body = new JsonObject();
    body.addProperty("username",  a.getUsername());
    body.addProperty("nombre",    a.getNombre());
    body.addProperty("apellidos", a.getApellidos());
    body.addProperty("telefono",  a.getTelefono());
    body.addProperty("email",     a.getEmail());
    body.addProperty("activo",    a.isActivo());
    // ← password nunca se incluye
    HttpResponse<String> resp = put("agentes/" + a.getId(), body.toString());
    ...
}
```

### Solución — Cambiar la firma del método para aceptar contraseña opcional

```java
public static void editarAgente(Agente a, String nuevaPassword) {
    JsonObject body = new JsonObject();
    body.addProperty("username",  a.getUsername());
    body.addProperty("nombre",    a.getNombre());
    body.addProperty("apellidos", a.getApellidos());
    body.addProperty("telefono",  a.getTelefono());
    body.addProperty("email",     a.getEmail());
    body.addProperty("activo",    a.isActivo());
    // Solo incluir password si el usuario escribió una nueva
    if (nuevaPassword != null && !nuevaPassword.isBlank()) {
        body.addProperty("password", nuevaPassword);
    }

    try {
        HttpResponse<String> resp = put("agentes/" + a.getId(), body.toString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            System.out.println("✓ Agente actualizado");
        } else {
            System.err.println("Error al editar agente: " + resp.body());
        }
    } catch (Exception e) {
        System.err.println("Error al editar agente: " + e.getMessage());
    }
}
```

> **Importante para el backend:** El endpoint `PUT /api/agentes/:id` debe hashear la contraseña con `bcrypt` de Node.js **solo si llega el campo `password`** en el body. Si no llega, no tocar el hash almacenado. Usar el mismo módulo `bcrypt` (npm) que usa el endpoint de login, NO `pgcrypto` de PostgreSQL, porque los hashes son incompatibles.

---

## Error 5 — El parseo del campo `activo` en `mostrarAgentes()` puede fallar

**Archivo:** `Conexion.java` línea 1254  
**Severidad:** Media  
**Estado: ✅ REPARADO** — Parseo robusto que soporta boolean JSON, string "true"/"t"/"1".

### Problema
```java
"true".equalsIgnoreCase(str(obj, "activo")) || "t".equalsIgnoreCase(str(obj, "activo"))
```
El helper `str()` llama a `getAsString()` sobre el elemento JSON. Si la API devuelve `activo` como booleano JSON (`true`/`false`) funciona, pero si el campo llega como entero `1`/`0` (comportamiento de algunas versiones de Sequelize/Prisma con PostgreSQL boolean), la condición falla y todos los agentes aparecen como inactivos.

### Código actual
```java
Agente a = new Agente(
        str(obj, "username"),
        str(obj, "nombre"),
        str(obj, "apellidos"),
        str(obj, "telefono"),
        str(obj, "email"),
        "true".equalsIgnoreCase(str(obj, "activo")) || "t".equalsIgnoreCase(str(obj, "activo")));
```

### Solución — Parseo robusto del campo boolean
```java
// Reemplazar la línea del parámetro activo con:
JsonElement activoEl = obj.get("activo");
boolean activo = activoEl != null && !activoEl.isJsonNull() && (
        (activoEl.isJsonPrimitive() && activoEl.getAsJsonPrimitive().isBoolean()
                ? activoEl.getAsBoolean()
                : "true".equalsIgnoreCase(activoEl.getAsString())
                  || "t".equalsIgnoreCase(activoEl.getAsString())
                  || "1".equals(activoEl.getAsString()))
);

Agente a = new Agente(
        str(obj, "username"),
        str(obj, "nombre"),
        str(obj, "apellidos"),
        str(obj, "telefono"),
        str(obj, "email"),
        activo);
```

---

## Error 6 — `iniciarTablas()` carga agentes sin token en el arranque

**Archivo:** `controladorApp.java` (dentro del método `iniciarTablas()`)  
**Severidad:** Baja — fallo silencioso, no crash  
**Estado: ✅ REPARADO** — Se eliminó `tablaAgentes.setItems(...)` de `iniciarTablas()`. Solo quedan las columnas. Los datos se cargan en `irAAgentes()` vía `refrescarTablaAgentes()`.

### Problema
`iniciarTablas()` se llama en `initialize()` antes del login, cuando `agenteToken` es null. Si `GET /api/agentes` requiere autenticación, devuelve 401 y la tabla de agentes se inicializa vacía en silencio.

### Código actual (fragmento de `iniciarTablas()`)
```java
tablaAgentes.setItems(Conexion.mostrarAgentes());
colAgenteUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
colAgenteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
colAgenteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
```

### Solución — Solo configurar columnas en `iniciarTablas()`, cargar datos en `irAAgentes()`

Dejar en `iniciarTablas()` solo la configuración de columnas (sin llamada a datos):
```java
// En iniciarTablas() — solo configurar columnas, NO cargar datos
colAgenteUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
colAgenteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
colAgenteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
```

`irAAgentes()` ya llama a `refrescarTablaAgentes()` cuando el usuario navega al panel, con el token ya disponible. Eso es suficiente.

---

---

## Error 7 — Los campos del formulario de agentes no muestran error visual al validar

**Archivo:** `controladorApp.java` — método `guardarAgente()` (líneas 2629–2691)  
**Severidad:** Media — inconsistencia de UX con el resto de la app  
**Estado: ✅ REPARADO** — Se añadió `aplicarEfectoShake()` y `aplicarEstiloError()` en cada validación del formulario.

### Problema
En todos los demás paneles (login, clientes, alquileres, ingresos), cuando el usuario intenta guardar con datos inválidos los campos muestran un borde rojo y un efecto de vibración (`aplicarEfectoShake` + `aplicarEstiloError`). En el formulario de agentes solo aparece una alerta genérica pero los campos no cambian visualmente.

Los métodos `aplicarEfectoShake(TextField)` y `aplicarEstiloError(TextField)` ya existen en el controlador (líneas 3519 y 3536). Simplemente no se llaman desde `guardarAgente()`.

### Código actual (roto)
```java
// Solo muestra un Alert, no toca los campos visualmente
if (username.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
    AlertaEstilizada.mostrarAdvertencia("Campos incompletos", ...);
    return;
}
if (agenteEnEdicion == null && password.isEmpty()) {
    AlertaEstilizada.mostrarAdvertencia("Contraseña requerida", ...);
    return;
}
if (!validadorAgente.isValid()) {
    AlertaEstilizada.mostrarAdvertencia("Datos inválidos", ...);
    return;
}
```

### Solución — Añadir feedback visual en cada validación de `guardarAgente()`

Reemplazar los bloques de validación en `guardarAgente()` con:

```java
// Validar campos obligatorios con feedback visual
boolean hayErrores = false;
if (username.isEmpty()) {
    aplicarEfectoShake(txtAgenteUsername);
    aplicarEstiloError(txtAgenteUsername);
    hayErrores = true;
}
if (nombre.isEmpty()) {
    aplicarEfectoShake(txtAgenteNombre);
    aplicarEstiloError(txtAgenteNombre);
    hayErrores = true;
}
if (apellidos.isEmpty()) {
    aplicarEfectoShake(txtAgenteApellidos);
    aplicarEstiloError(txtAgenteApellidos);
    hayErrores = true;
}
if (hayErrores) {
    AlertaEstilizada.mostrarAdvertencia("Campos incompletos",
            "Datos requeridos", "Usuario, nombre y apellidos son obligatorios");
    return;
}

// Contraseña obligatoria solo al crear
if (agenteEnEdicion == null && password.isEmpty()) {
    aplicarEfectoShake(pfAgentePassword);
    aplicarEstiloError(pfAgentePassword);
    AlertaEstilizada.mostrarAdvertencia("Contraseña requerida",
            "Dato requerido", "La contraseña es obligatoria al crear un agente");
    return;
}

if (!password.isEmpty() && !password.matches(".{6,}")) {
    aplicarEfectoShake(pfAgentePassword);
    aplicarEstiloError(pfAgentePassword);
    AlertaEstilizada.mostrarAdvertencia("Contraseña débil",
            "Longitud mínima", "La contraseña debe tener al menos 6 caracteres");
    return;
}

// Validar teléfono y email con feedback visual
if (!txtAgenteTelefono.getText().trim().isEmpty() &&
        !txtAgenteTelefono.getText().trim().matches("\\d{9,15}")) {
    aplicarEfectoShake(txtAgenteTelefono);
    aplicarEstiloError(txtAgenteTelefono);
    AlertaEstilizada.mostrarAdvertencia("Teléfono inválido",
            "Formato incorrecto", "El teléfono debe tener entre 9 y 15 dígitos");
    return;
}

if (!txtAgenteEmail.getText().trim().isEmpty() &&
        !txtAgenteEmail.getText().trim().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
    aplicarEfectoShake(txtAgenteEmail);
    aplicarEstiloError(txtAgenteEmail);
    AlertaEstilizada.mostrarAdvertencia("Email inválido",
            "Formato incorrecto", "Introduce un email válido (ejemplo: nombre@dominio.com)");
    return;
}
```

> **Nota:** `pfAgentePassword` es un `PasswordField` que extiende `TextField`, por lo que `aplicarEfectoShake` y `aplicarEstiloError` funcionan sin cambios.

---

## Resumen de todos los cambios necesarios

| # | Estado | Archivo | Descripción |
|---|--------|---------|-------------|
| 1 | ✅ Reparado | `controladorApp.java` | `private Agente agenteEnEdicion = null;` — campo añadido |
| 2 | ✅ Reparado | `controladorApp.java` | `editarAgente()` — solo carga datos, ya no guarda inmediatamente |
| 3 | ✅ Reparado | `controladorApp.java` | `guardarAgente()` — ya maneja crear y editar, valida con `isValid()` |
| 4 | ✅ Reparado | `controladorApp.java` | `cancelarNuevoAgente()` — ya resetea `agenteEnEdicion = null` |
| 5 | ✅ Reparado | `ValidadorAgente.java` | `validarYMostrarErrores()` — ya existe |
| 6 | ✅ Reparado | `Conexion.java` + `controladorApp.java` | Cambiar firma a `editarAgente(Agente a, String nuevaPassword)` y pasar la contraseña desde el controlador |
| 7 | ✅ Reparado | `Conexion.java` | Parseo robusto del campo `activo` en `mostrarAgentes()` |
| 8 | ✅ Reparado | `controladorApp.java` | Quitar `tablaAgentes.setItems(Conexion.mostrarAgentes())` de `iniciarTablas()` |
| 9 | ✅ Reparado | `controladorApp.java` método `guardarAgente()` | Añadir `aplicarEfectoShake` + `aplicarEstiloError` en cada validación del formulario de agentes |

---

## Contexto importante para el backend (no es código Java)

El endpoint `PUT /api/agentes/:id` del backend Node.js debe:
1. Si llega el campo `password` en el body → hashear con `bcrypt.hash(password, 12)` (npm bcrypt) y guardar
2. Si NO llega el campo `password` → no tocar el hash almacenado
3. **No usar `pgcrypto.crypt()` de PostgreSQL** para hashear — los hashes `$2a$` que genera son incompatibles con `bcrypt.compare()` de Node.js que usa el login

El endpoint `POST /api/agentes` (crear) ya debe hacer lo mismo: hashear siempre con npm bcrypt.
