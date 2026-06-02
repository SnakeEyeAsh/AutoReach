# Implementación — Chat con IA (Groq/Llama) en AutoReach

**Proyecto:** AutoReach — app de escritorio JavaFX, gestión de alquiler/venta de vehículos
**Stack:** JavaFX 21, JDK 17, Gradle 8, `java.net.http.HttpClient`, Gson
**API utilizada:** Groq (OpenAI-compatible) con modelo `llama-3.3-70b-versatile`
**Objetivo:** Panel de chat donde el operador haga preguntas en lenguaje natural sobre los datos actuales de la BD (clientes, coches, alquileres, agentes) y la IA responda con esa información como contexto.

---

## Resumen de cambios

| Archivo | Tipo | Cambio |
|---------|------|--------|
| `Application.properties` | Modificar | Añadir clave de API |
| `GeminiService.java` | **Crear nuevo** | Clase que llama a la API de Groq (OpenAI-compatible) |
| `cambiarColores.java` | Modificar | Añadir sobrecarga de 6 botones |
| `pantalla1.fxml` | Modificar | Añadir botón sidebar + panel de chat |
| `controladorApp.java` | Modificar | Añadir campos FXML, handlers y lógica del chat |

---

## Paso 1 — `Application.properties`

**Ruta:** `app/src/main/resources/Application.properties`

Añadir al final del archivo:

```properties
###############################################
#  GROQ IA (OpenAI-compatible)
###############################################

groq.api.key=YOUR_GROQ_API_KEY_HERE
```

> Obtener API key gratuita en: https://console.groq.com/keys
> Modelo usado: `llama-3.3-70b-versatile` (tier gratuito: 30 RPM, 6,000 RPD, suficiente para uso interno).
> La API es compatible con el formato de OpenAI (`/v1/chat/completions`).

---

## Paso 2 — Crear `GeminiService.java`

**Ruta:** `app/src/main/java/com/javafx/Service/GeminiService.java`
*(Crear el directorio `Service` si no existe)*

```java
package com.javafx.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javafx.JDBC.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GeminiService {

    private static final String BASE_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL    = "llama-3.3-70b-versatile";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey;

    private final List<JsonObject> historial = new ArrayList<>();
    private String contextoSistema = "";

    public GeminiService() {
        this.apiKey = Config.get("groq.api.key");
    }

    public void setContexto(String contexto) {
        this.contextoSistema = contexto;
        historial.clear();
    }

    public String enviarMensaje(String mensajeUsuario) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("Clave de API no configurada en Application.properties (groq.api.key)");
        }

        JsonObject turnoUsuario = new JsonObject();
        turnoUsuario.addProperty("role", "user");
        turnoUsuario.addProperty("content", mensajeUsuario);
        historial.add(turnoUsuario);

        JsonArray messages = new JsonArray();

        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content", contextoSistema);
        messages.add(system);

        for (JsonObject turno : historial) {
            messages.add(turno);
        }

        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.add("messages", messages);
        body.addProperty("temperature", 0.3);
        body.addProperty("max_tokens", 1024);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String msg;
            if (response.statusCode() == 429) {
                msg = "Límite de cuota de API alcanzado. Espera un minuto y vuelve a intentarlo.";
            } else {
                msg = "Error API: HTTP " + response.statusCode();
            }
            throw new Exception(msg);
        }

        String textoRespuesta = JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        JsonObject turnoAsistente = new JsonObject();
        turnoAsistente.addProperty("role", "assistant");
        turnoAsistente.addProperty("content", textoRespuesta);
        historial.add(turnoAsistente);

        return textoRespuesta;
    }

    public void limpiarHistorial() {
        historial.clear();
    }
}
```

### Notas sobre `GeminiService.java`

- El nombre de la clase es `GeminiService` (por compatibilidad, aunque internamente use Groq)
- Usa formato **OpenAI-compatible** (`messages[]` con `role`: `system`/`user`/`assistant`)
- La API key se pasa como `Authorization: Bearer <key>` (estándar OpenAI)
- El historial de la conversación se mantiene en memoria (se pierde al cerrar el panel)
- En cada petición se envía el `system` message (contexto de la BD) + todo el historial
- El `system` message contiene solo conteos de registros (no los datos completos) para ahorrar tokens
- Maneja el error HTTP 429 (cuota excedida) con un mensaje amigable

---

## Paso 3 — `cambiarColores.java`

**Ruta:** `app/src/main/java/com/javafx/App/cambiarColores.java`

Añadir la siguiente sobrecarga al final de la clase, antes del cierre `}`:

```java
public static void cambiar(Button presente, Button dos, Button tres,
                            Button cuatro, Button cinco, Button seis) {
    presente.setStyle(ACTIVO);
    dos.setStyle(INACTIVO);
    tres.setStyle(INACTIVO);
    cuatro.setStyle(INACTIVO);
    cinco.setStyle(INACTIVO);
    seis.setStyle(INACTIVO);
}
```

Esto permite manejar 6 botones en la barra lateral (los 5 originales + `btnChat`).

---

## Paso 4 — `pantalla1.fxml`

**Ruta:** `app/src/main/resources/pantalla1.fxml`

### 4a — Import de `ScrollPane`

Añadir junto a los imports existentes:

```xml
<?import javafx.scene.control.ScrollPane?>
```

### 4b — Botón en la barra lateral

Después del botón `btnAgentes` y **antes** del botón `btnAcercaDe`, añadir:

```xml
<Button fx:id="btnChat" contentDisplay="TOP" maxHeight="Infinity" maxWidth="Infinity"
        mnemonicParsing="false" onAction="#irAChat" prefWidth="110.0"
        style="-fx-background-color: transparent; -fx-text-fill: #b0bcd4;
               -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-padding: 10 0;"
        text="IA Chat" VBox.vgrow="ALWAYS">
   <tooltip><Tooltip text="Chat con Asistente IA" /></tooltip>
   <graphic>
      <ImageView fitHeight="36.0" fitWidth="36.0" pickOnBounds="true" preserveRatio="true">
         <image><Image url="@Assets/perfil.png" /></image>
      </ImageView>
   </graphic>
</Button>
```

### 4c — Panel de chat

Después del cierre del panel `panelAgentes` (`</Pane>`) y **antes** del panel `panelRegistro`, añadir:

```xml
<!-- ── CHAT CON IA ─────────────────────────────── -->
<Pane fx:id="panelChat" prefHeight="578.0" prefWidth="724.0" visible="false"
      style="-fx-background-color: #f0f4f8;">
   <children>

      <!-- Cabecera -->
      <Pane layoutX="0" layoutY="0" prefHeight="56" prefWidth="724"
            style="-fx-background-color: #1a1a2e;">
         <children>
            <Label layoutX="24" layoutY="16" text="Asistente IA — AutoReach"
                   style="-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;
                          -fx-font-family: 'Segoe UI';"/>
            <Label fx:id="lblEstadoChat" layoutX="420" layoutY="20" text=""
                   style="-fx-text-fill: #00d4ff; -fx-font-size: 11; -fx-font-family: 'Segoe UI';"/>
            <Button fx:id="btnLimpiarChat" layoutX="640" layoutY="14"
                    mnemonicParsing="false" onAction="#limpiarChat" text="Limpiar"
                    style="-fx-background-color: transparent; -fx-text-fill: #b0bcd4;
                           -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;
                           -fx-border-color: #b0bcd4; -fx-border-radius: 4; -fx-padding: 4 10;"/>
         </children>
      </Pane>

      <!-- Área de mensajes -->
      <ScrollPane fx:id="scrollChat" layoutX="0" layoutY="56" prefHeight="452" prefWidth="724"
                  fitToWidth="true" hbarPolicy="NEVER"
                  style="-fx-background: #f0f4f8; -fx-background-color: #f0f4f8;
                         -fx-border-color: transparent;">
         <content>
            <VBox fx:id="vboxMensajes" fillWidth="true" spacing="10"
                  style="-fx-padding: 16; -fx-background-color: #f0f4f8;">
               <Label text="Hola, soy el asistente de AutoReach. Puedes preguntarme sobre clientes,
coches, alquileres y agentes del sistema."
                      wrapText="true" maxWidth="560"
                      style="-fx-background-color: #1a1a2e; -fx-text-fill: #e0e6f0;
                             -fx-padding: 12 16; -fx-background-radius: 12;
                             -fx-font-family: 'Segoe UI'; -fx-font-size: 12;"/>
            </VBox>
         </content>
      </ScrollPane>

      <!-- Barra de entrada -->
      <Pane layoutX="0" layoutY="508" prefHeight="70" prefWidth="724"
            style="-fx-background-color: #1a1a2e; -fx-border-color: #0076a8;
                   -fx-border-width: 1 0 0 0;">
         <children>
            <TextField fx:id="txtMensajeChat" layoutX="16" layoutY="18"
                       prefWidth="590" prefHeight="34"
                       promptText="Escribe tu pregunta..."
                       onAction="#enviarMensajeChat"
                       style="-fx-background-color: #252540; -fx-text-fill: white;
                              -fx-prompt-text-fill: #6b7a99; -fx-font-family: 'Segoe UI';
                              -fx-font-size: 13; -fx-background-radius: 8;
                              -fx-border-color: #0076a8; -fx-border-radius: 8;
                              -fx-padding: 0 12;"/>
            <Button fx:id="btnEnviarChat" layoutX="618" layoutY="18"
                    mnemonicParsing="false" onAction="#enviarMensajeChat"
                    prefWidth="90" prefHeight="34" text="Enviar"
                    style="-fx-background-color: #0076a8; -fx-text-fill: white;
                           -fx-font-family: 'Segoe UI'; -fx-font-size: 13;
                           -fx-background-radius: 8; -fx-cursor: hand;"/>
         </children>
      </Pane>

   </children>
</Pane>
```

---

## Paso 5 — `controladorApp.java`

**Ruta:** `app/src/main/java/com/javafx/App/controladorApp.java`

### 5a — Import nuevo

Añadir junto al resto de imports:

```java
import com.javafx.Service.GeminiService;
import javafx.scene.control.ScrollPane;
```

### 5b — Campos FXML nuevos

Añadir junto al bloque de campos `@FXML` (cerca de `panelAgentes`):

```java
@FXML private Pane panelChat;
@FXML private Button btnChat;
@FXML private TextField txtMensajeChat;
@FXML private VBox vboxMensajes;
@FXML private ScrollPane scrollChat;
@FXML private Label lblEstadoChat;
@FXML private Button btnEnviarChat;
@FXML private Button btnLimpiarChat;
```

### 5c — Instancia del servicio

Añadir junto a los otros campos privados (cerca de `private ValidadorAgente validadorAgente`):

```java
private final GeminiService geminiService = new GeminiService();
```

### 5d — `panelChat` en `ocultarTodos()`

```java
private void ocultarTodos() {
    panelVentas.setVisible(false);
    pantallaAlquiler.setVisible(false);
    pantallaSesion.setVisible(false);
    panelRegistro.setVisible(false);
    panelIngreso.setVisible(false);
    panelAgentes.setVisible(false);
    panelChat.setVisible(false);          // ← AÑADIR
}
```

### 5e — `btnChat` en `desactivarBotones()`

```java
private void desactivarBotones() {
    btnAlquiler.setDisable(true);
    btnIrAInicio.setDisable(true);
    btnCompras.setDisable(true);
    btnCerrarSesion.setDisable(true);
    btnIrAIngreso.setDisable(true);
    btnAgentes.setDisable(true);
    btnChat.setDisable(true);             // ← AÑADIR
}
```

### 5f — Habilitar `btnChat` tras login

Dentro de `IniciarSesion()` (tras login exitoso):

```java
btnAlquiler.setDisable(false);
btnIrAInicio.setDisable(false);
btnCerrarSesion.setDisable(false);
btnIrAIngreso.setDisable(false);
btnCompras.setDisable(false);
btnAgentes.setDisable(false);
btnChat.setDisable(false);               // ← AÑADIR
```

### 5g — Actualizar todas las llamadas a `cambiarColores.cambiar()`

Cada llamada debe incluir `btnChat` como sexto parámetro. Son 6 llamadas:

```java
// En initialize():
cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);

// En irAInicio():
cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);

// En irAalquiler():
cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso, btnAgentes, btnChat);

// En irAcompras():
cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso, btnAgentes, btnChat);

// En irAIngresos():
cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnAgentes, btnChat);

// En irAAgentes():
cambiarColores.cambiar(btnAgentes, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnChat);
```

### 5h — Métodos del chat

Añadir estos métodos después de `irAAgentes()`:

```java
@FXML
void irAChat(ActionEvent event) {
    mostrarPanel(panelChat);
    cambiarColores.cambiar(btnChat, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes);
    new Thread(() -> {
        String contexto = construirContextoGemini();
        geminiService.setContexto(contexto);
        javafx.application.Platform.runLater(() ->
            lblEstadoChat.setText("\u2713 Datos cargados"));
    }).start();
}

@FXML
void enviarMensajeChat(ActionEvent event) {
    String mensaje = txtMensajeChat.getText().trim();
    if (mensaje.isEmpty()) return;

    agregarMensajeChat(mensaje, true);
    txtMensajeChat.clear();
    btnEnviarChat.setDisable(true);
    lblEstadoChat.setText("Pensando...");

    new Thread(() -> {
        try {
            String respuesta = geminiService.enviarMensaje(mensaje);
            javafx.application.Platform.runLater(() -> {
                agregarMensajeChat(respuesta, false);
                btnEnviarChat.setDisable(false);
                lblEstadoChat.setText("");
            });
        } catch (Exception e) {
            javafx.application.Platform.runLater(() -> {
                agregarMensajeChat("Error: " + e.getMessage(), false);
                btnEnviarChat.setDisable(false);
                lblEstadoChat.setText("Error");
            });
        }
    }).start();
}

@FXML
void limpiarChat(ActionEvent event) {
    vboxMensajes.getChildren().clear();
    geminiService.limpiarHistorial();
    lblEstadoChat.setText("");
    agregarMensajeChat("Hola, soy el asistente de AutoReach. Puedes preguntarme sobre " +
            "clientes, coches, alquileres y agentes del sistema.", false);
}

private void agregarMensajeChat(String texto, boolean esUsuario) {
    javafx.scene.control.Label burbuja = new javafx.scene.control.Label(texto);
    burbuja.setWrapText(true);
    burbuja.setMaxWidth(560);
    burbuja.setStyle(esUsuario
            ? "-fx-background-color: #0076a8; -fx-text-fill: white; " +
              "-fx-padding: 12 16; -fx-background-radius: 12; " +
              "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;"
            : "-fx-background-color: #1a1a2e; -fx-text-fill: #e0e6f0; " +
              "-fx-padding: 12 16; -fx-background-radius: 12; " +
              "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

    javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(burbuja);
    contenedor.setAlignment(esUsuario
            ? javafx.geometry.Pos.CENTER_RIGHT
            : javafx.geometry.Pos.CENTER_LEFT);

    vboxMensajes.getChildren().add(contenedor);

    javafx.application.Platform.runLater(() ->
        scrollChat.setVvalue(1.0));
}

private String construirContextoGemini() {
    StringBuilder ctx = new StringBuilder();
    ctx.append("Eres el asistente inteligente de AutoReach, un sistema de gestión de ");
    ctx.append("alquiler y venta de vehículos de una concesionaria. ");
    ctx.append("Responde siempre en español, de forma concisa y precisa. ");
    ctx.append("Solo responde preguntas relacionadas con los datos de AutoReach.\n\n");

    try {
        javafx.collections.ObservableList<com.javafx.Model.Cliente> clientes =
                com.javafx.JDBC.Conexion.mostrarClientes();
        ctx.append("=== CLIENTES: ").append(clientes.size()).append(" registrados.\n");

        javafx.collections.ObservableList<com.javafx.Model.Coche> coches =
                com.javafx.JDBC.Conexion.mostrarCoches();
        ctx.append("=== COCHES DISPONIBLES: ").append(coches.size()).append(" unidades.\n");

        javafx.collections.ObservableList<com.javafx.Model.Alquiler> alquileres =
                com.javafx.JDBC.Conexion.mostrarAlquiler();
        ctx.append("=== ALQUILERES ACTIVOS: ").append(alquileres.size()).append(" registros.\n");

        javafx.collections.ObservableList<com.javafx.Model.Agente> agentes =
                com.javafx.JDBC.Conexion.mostrarAgentes();
        ctx.append("=== AGENTES DEL SISTEMA: ").append(agentes.size()).append(" operadores.\n");

    } catch (Exception e) {
        ctx.append("\n[Error al cargar datos: ").append(e.getMessage()).append("]\n");
    }

    return ctx.toString();
}
```

---

## Notas importantes

1. **API Key**: La propiedad en `Application.properties` se lee mediante `Config.get("groq.api.key")` — la clase `Config` ya existe en el proyecto y lee el archivo automáticamente.

2. **Conexion.mostrarX()**: Los métodos `mostrarClientes()`, `mostrarCoches()`, `mostrarAlquiler()`, `mostrarAgentes()` existen en `Conexion.java` y devuelven `ObservableList<T>`.

3. **Modelos usados por el contexto**:
   - `Cliente` tiene: `getNombre()`, `getApellidos()`, `getDni()`, `getTelefono()`, `getEmail()`
   - `Coche` tiene: `getMarca()`, `getModelo()`, `getMatricula()`, `getAño()`, `getColor()`, `getPrecio()`
   - `Alquiler` tiene: `getMatricula()`, `getDni()`, `getFechaDevolucionStr()`
   - `Agente` tiene: `getNombre()`, `getApellidos()`, `getUsername()`, `getEmail()`, `isActivo()`

4. **Ahorro de tokens**: El contexto solo incluye conteos, no los datos completos de cada registro. Esto evita agotar la cuota gratuita de la API.

5. **Manejo de errores**: El error HTTP 429 (cuota excedida) se muestra como mensaje amigable en el chat, sin imprimir el JSON de error en consola.

6. **API Groq**: Usa formato **OpenAI-compatible**:
   - Endpoint: `POST https://api.groq.com/openai/v1/chat/completions`
   - Auth: `Authorization: Bearer <key>`
   - Body: `{ "model": "...", "messages": [...], "temperature": 0.3, "max_tokens": 1024 }`

7. **Alternativa — Gemini API**: Si se prefiere usar Gemini en lugar de Groq:
   - Cambiar `BASE_URL` a `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=`
   - Cambiar el body a formato Gemini (`systemInstruction`, `contents[]`)
   - Cambiar el parsing de respuesta a `response.candidates[0].content.parts[0].text`
   - La clave se pasa como query parameter `?key=...` en lugar de `Authorization` header

---

## Comportamiento esperado

- El botón **"IA Chat"** aparece en la barra lateral con el mismo estilo que los demás botones
- Al hacer login como agente, el botón se habilita (inicialmente está deshabilitado)
- Al pulsar **"IA Chat"**, se cargan los conteos de la BD en segundo plano (el label muestra "✓ Datos cargados")
- El operador puede escribir preguntas como:
  - *"¿Cuántos clientes tenemos?"*
  - *"¿Qué coches BMW están disponibles?"*
  - *"¿Hay alquileres que vencen esta semana?"*
  - *"¿Cuántos agentes están activos?"*
- Los mensajes del usuario aparecen a la derecha (fondo azul `#0076a8`), las respuestas de la IA a la izquierda (fondo oscuro `#1a1a2e`)
- El botón **"Limpiar"** borra la conversación y reinicia el historial
- Si la API key no está configurada, muestra un mensaje de error claro en el chat
- Si se excede la cuota (HTTP 429), muestra un mensaje amigable en lugar del JSON de error
