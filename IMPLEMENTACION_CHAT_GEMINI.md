# Implementación — Chat con Gemini IA en AutoReach

**Proyecto:** AutoReach — app de escritorio JavaFX, gestión de alquiler/venta de vehículos  
**Stack:** JavaFX 21, JDK 17, Gradle 8, `java.net.http.HttpClient`, Gson  
**Objetivo:** Añadir un panel de chat donde el operador pueda hacer preguntas en lenguaje natural sobre los datos actuales de la BD (clientes, coches, alquileres, agentes) y Gemini responda con esa información como contexto.

---

## Resumen de cambios

| Archivo | Tipo | Qué hacer |
|---------|------|-----------|
| `Application.properties` | Modificar | Añadir clave de API de Gemini |
| `GeminiService.java` | **Crear nuevo** | Clase que llama a la API de Gemini |
| `cambiarColores.java` | Modificar | Añadir sobrecarga de 6 botones |
| `pantalla1.fxml` | Modificar | Añadir botón sidebar + panel de chat |
| `controladorApp.java` | Modificar | Añadir campos FXML, handlers y lógica del chat |

---

## Paso 1 — `Application.properties`

**Ruta:** `app/src/main/resources/Application.properties`

Añadir al final del archivo:

```properties
###############################################
#  GEMINI IA
###############################################

gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

> Obtener la API key gratuita en: https://aistudio.google.com/app/apikey  
> El modelo usado es `gemini-2.0-flash` (tier gratuito: 15 RPM, suficiente para uso interno).

---

## Paso 2 — Crear `GeminiService.java`

**Ruta:** `app/src/main/java/com/javafx/Service/GeminiService.java`  
*(Crear el directorio `Service` si no existe)*

```java
package com.javafx.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javafx.Data.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GeminiService {

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey;

    // Historial de la conversación actual (se reinicia al abrir el panel)
    private final List<JsonObject> historial = new ArrayList<>();

    // Contexto del sistema (datos de la BD) — se construye al abrir el chat
    private String contextoSistema = "";

    public GeminiService() {
        this.apiKey = Config.get("gemini.api.key");
    }

    /** Establece el contexto del sistema con los datos actuales de la BD. */
    public void setContexto(String contexto) {
        this.contextoSistema = contexto;
        historial.clear();
    }

    /**
     * Envía un mensaje al chat y devuelve la respuesta de Gemini.
     * Mantiene el historial de la conversación para respuestas contextuales.
     */
    public String enviarMensaje(String mensajeUsuario) throws Exception {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            throw new Exception("Clave de API de Gemini no configurada en Application.properties");
        }

        // Añadir mensaje del usuario al historial
        JsonObject turnoUsuario = new JsonObject();
        turnoUsuario.addProperty("role", "user");
        JsonArray partsUsuario = new JsonArray();
        JsonObject partUsuario = new JsonObject();
        partUsuario.addProperty("text", mensajeUsuario);
        partsUsuario.add(partUsuario);
        turnoUsuario.add("parts", partsUsuario);
        historial.add(turnoUsuario);

        // Construir body de la petición
        JsonObject body = new JsonObject();

        // Instrucción de sistema con contexto de la BD
        JsonObject systemInstruction = new JsonObject();
        JsonArray systemParts = new JsonArray();
        JsonObject systemPart = new JsonObject();
        systemPart.addProperty("text", contextoSistema);
        systemParts.add(systemPart);
        systemInstruction.add("parts", systemParts);
        body.add("systemInstruction", systemInstruction);

        // Historial de conversación
        JsonArray contents = new JsonArray();
        for (JsonObject turno : historial) {
            contents.add(turno);
        }
        body.add("contents", contents);

        // Configuración de generación
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.3);
        generationConfig.addProperty("maxOutputTokens", 1024);
        body.add("generationConfig", generationConfig);

        // Hacer la petición HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Error Gemini API: HTTP " + response.statusCode()
                    + " — " + response.body());
        }

        // Parsear respuesta
        JsonObject respJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String textoRespuesta = respJson
                .getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        // Añadir respuesta del modelo al historial
        JsonObject turnoModelo = new JsonObject();
        turnoModelo.addProperty("role", "model");
        JsonArray partsModelo = new JsonArray();
        JsonObject partModelo = new JsonObject();
        partModelo.addProperty("text", textoRespuesta);
        partsModelo.add(partModelo);
        turnoModelo.add("parts", partsModelo);
        historial.add(turnoModelo);

        return textoRespuesta;
    }

    /** Limpia el historial de conversación. */
    public void limpiarHistorial() {
        historial.clear();
    }
}
```

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

---

## Paso 4 — `pantalla1.fxml`

**Ruta:** `app/src/main/resources/pantalla1.fxml`

### 4a — Descargar el icono de Gemini

**Antes de tocar el FXML**, descargar el icono oficial de Gemini y colocarlo en la carpeta de assets del proyecto:

El archivo `gemini-color.png` ya está en `app/src/main/resources/Assets/gemini-color.png`. No hay que descargar nada.

### 4b — Añadir botón en la barra lateral

Buscar el bloque del botón `btnAgentes` (termina en `</Button>` con la línea del tooltip de "Gestionar agentes del sistema") y añadir justo **después** de su cierre `</Button>`:

```xml
<Button fx:id="btnChat" contentDisplay="TOP" maxHeight="Infinity" maxWidth="Infinity"
        mnemonicParsing="false" onAction="#irAChat" prefWidth="110.0"
        style="-fx-background-color: transparent; -fx-text-fill: #b0bcd4;
               -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-padding: 10 0;"
        text="IA Chat" VBox.vgrow="ALWAYS">
   <tooltip><Tooltip text="Chat con Asistente IA — Gemini" /></tooltip>
   <graphic>
      <ImageView fitHeight="36.0" fitWidth="36.0" pickOnBounds="true" preserveRatio="true">
         <image><Image url="@Assets/gemini-color.png" /></image>
      </ImageView>
   </graphic>
</Button>
```

### 4c — Añadir el panel de chat

Buscar el cierre del panel de agentes (`<!-- fin panelAgentes -->` o el `</Pane>` que corresponde a `panelAgentes`) y añadir justo **después**:

```xml
<!-- ── CHAT CON GEMINI IA ─────────────────────────────── -->
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
               <!-- Los mensajes se añaden aquí dinámicamente desde Java -->
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

Añadir junto al resto de imports al principio del archivo:

```java
import com.javafx.Service.GeminiService;
```

### 5b — Campos FXML nuevos

Añadir junto al bloque de campos `@FXML` existentes (cerca de `panelAgentes`):

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

### 5c — Campo de instancia del servicio

Añadir junto a los otros campos privados del controlador (cerca de `private ValidadorAgente validadorAgente;`):

```java
private final GeminiService geminiService = new GeminiService();
```

### 5d — Añadir `panelChat` a `ocultarTodos()`

Buscar el método `ocultarTodos()` y añadir la línea que falta:

```java
private void ocultarTodos() {
    panelVentas.setVisible(false);
    pantallaAlquiler.setVisible(false);
    pantallaSesion.setVisible(false);
    panelRegistro.setVisible(false);
    panelIngreso.setVisible(false);
    panelAgentes.setVisible(false);
    panelChat.setVisible(false);          // ← AÑADIR ESTA LÍNEA
}
```

### 5e — Añadir `btnChat` a `desactivarBotones()`

```java
private void desactivarBotones() {
    btnAlquiler.setDisable(true);
    btnIrAInicio.setDisable(true);
    btnCompras.setDisable(true);
    btnCerrarSesion.setDisable(true);
    btnIrAIngreso.setDisable(true);
    btnAgentes.setDisable(true);
    btnChat.setDisable(true);             // ← AÑADIR ESTA LÍNEA
}
```

### 5f — Habilitar `btnChat` tras el login

Buscar el bloque dentro de `IniciarSesion()` donde se habilitan los otros botones tras login exitoso y añadir:

```java
btnAlquiler.setDisable(false);
btnIrAInicio.setDisable(false);
btnCerrarSesion.setDisable(false);
btnIrAIngreso.setDisable(false);
btnCompras.setDisable(false);
btnChat.setDisable(false);               // ← AÑADIR ESTA LÍNEA
```

### 5g — Actualizar todas las llamadas a `cambiarColores.cambiar()` de 5 parámetros a 6

Buscar todas las ocurrencias de `cambiarColores.cambiar(` que tengan 5 parámetros y añadir `btnChat` al final de cada una. Son exactamente estas 5 líneas:

```java
// ANTES (buscar estas líneas):
cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes);
cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso, btnAgentes);
cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso, btnAgentes);
cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnAgentes);
cambiarColores.cambiar(btnAgentes, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso);

// DESPUÉS (reemplazar por estas):
cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnAgentes, btnChat);
cambiarColores.cambiar(btnAgentes, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnChat);
```

### 5h — Añadir los métodos del chat

Añadir estos métodos en el bloque de navegación entre paneles (cerca de `irAAgentes()`):

```java
@FXML
void irAChat(ActionEvent event) {
    mostrarPanel(panelChat);
    cambiarColores.cambiar(btnChat, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes);
    // Construir contexto con datos actuales y cargar en el servicio
    new Thread(() -> {
        String contexto = construirContextoGemini();
        geminiService.setContexto(contexto);
        javafx.application.Platform.runLater(() ->
            lblEstadoChat.setText("✓ Datos cargados"));
    }).start();
}

@FXML
void enviarMensajeChat(ActionEvent event) {
    String mensaje = txtMensajeChat.getText().trim();
    if (mensaje.isEmpty()) return;

    // Mostrar mensaje del usuario
    agregarMensajeChat(mensaje, true);
    txtMensajeChat.clear();
    btnEnviarChat.setDisable(true);
    lblEstadoChat.setText("Pensando...");

    // Llamar a Gemini en hilo de fondo
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
    // Volver a mostrar el mensaje de bienvenida
    agregarMensajeChat("Hola, soy el asistente de AutoReach. Puedes preguntarme sobre " +
            "clientes, coches, alquileres y agentes del sistema.", false);
}

// ─── Añade una burbuja de mensaje al chat ───────────────────────────────────
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

    // Auto-scroll al último mensaje
    javafx.application.Platform.runLater(() ->
        scrollChat.setVvalue(1.0));
}

// ─── Construye el prompt de sistema con datos actuales de la BD ─────────────
private String construirContextoGemini() {
    StringBuilder ctx = new StringBuilder();
    ctx.append("Eres el asistente inteligente de AutoReach, un sistema de gestión de ");
    ctx.append("alquiler y venta de vehículos de una concesionaria. ");
    ctx.append("Responde siempre en español, de forma concisa y precisa. ");
    ctx.append("Solo responde preguntas relacionadas con los datos de AutoReach.\n\n");

    try {
        // Clientes
        javafx.collections.ObservableList<com.javafx.Model.Cliente> clientes =
                com.javafx.JDBC.Conexion.mostrarClientes();
        ctx.append("=== CLIENTES REGISTRADOS (").append(clientes.size()).append(") ===\n");
        for (com.javafx.Model.Cliente c : clientes) {
            ctx.append("- ").append(c.getNombre()).append(" ").append(c.getApellidos())
               .append(" | DNI: ").append(c.getDni())
               .append(" | Tel: ").append(c.getTelefono())
               .append(" | Email: ").append(c.getEmail()).append("\n");
        }

        // Coches disponibles
        javafx.collections.ObservableList<com.javafx.Model.Coche> coches =
                com.javafx.JDBC.Conexion.mostrarCoches();
        ctx.append("\n=== COCHES DISPONIBLES (").append(coches.size()).append(") ===\n");
        for (com.javafx.Model.Coche c : coches) {
            ctx.append("- ").append(c.getMarca()).append(" ").append(c.getModelo())
               .append(" (").append(c.getAño()).append(") | Matrícula: ").append(c.getMatricula())
               .append(" | Color: ").append(c.getColor())
               .append(" | Precio: ").append(c.getPrecio()).append("€\n");
        }

        // Alquileres activos
        javafx.collections.ObservableList<com.javafx.Model.Alquiler> alquileres =
                com.javafx.JDBC.Conexion.mostrarAlquiler();
        ctx.append("\n=== ALQUILERES ACTIVOS (").append(alquileres.size()).append(") ===\n");
        for (com.javafx.Model.Alquiler a : alquileres) {
            ctx.append("- Matrícula: ").append(a.getMatricula())
               .append(" | DNI cliente: ").append(a.getDni())
               .append(" | Devolución: ").append(a.getFechaDevolucion()).append("\n");
        }

        // Agentes
        javafx.collections.ObservableList<com.javafx.Model.Agente> agentes =
                com.javafx.JDBC.Conexion.mostrarAgentes();
        ctx.append("\n=== AGENTES DEL SISTEMA (").append(agentes.size()).append(") ===\n");
        for (com.javafx.Model.Agente a : agentes) {
            ctx.append("- ").append(a.getNombre()).append(" ").append(a.getApellidos())
               .append(" | Usuario: ").append(a.getUsername())
               .append(" | Email: ").append(a.getEmail())
               .append(" | Activo: ").append(a.isActivo() ? "Sí" : "No").append("\n");
        }

    } catch (Exception e) {
        ctx.append("\n[Error al cargar datos: ").append(e.getMessage()).append("]\n");
    }

    return ctx.toString();
}
```

---

## Notas importantes para la compilación

1. **El directorio `Service`** debe existir en `app/src/main/java/com/javafx/Service/`. Crearlo si no existe.

2. **`Config.get("gemini.api.key")`** — el proyecto ya tiene una clase `Config` que lee `Application.properties`. Usar exactamente ese método.

3. **`Coche.getPrecio()`** — verificar el nombre exacto del getter en el modelo `Coche.java`. Si no existe `getPrecio()`, usar el getter correcto (puede ser `getPrecioBase()` o similar).

4. **`Alquiler.getFechaDevolucion()`** — verificar el nombre del getter en `Alquiler.java`. Puede ser `getFechaDevolucionStr()` u otro. Usar el que devuelva String para facilitar la concatenación.

5. **El modelo `Alquiler`** tiene `getFechaDevolucion()` que devuelve `Timestamp`. Convertirlo con `.toString()` o usar el getter de String si existe.

6. **La API key de Gemini** no debe commitearse al repositorio. Añadir `Application.properties` al `.gitignore` si no está ya.

---

## Comportamiento esperado

- El botón "IA Chat" aparece en la barra lateral con el mismo estilo que los demás botones
- Al pulsar "IA Chat", el panel carga los datos de la BD en segundo plano (el label muestra "✓ Datos cargados")
- El operador puede escribir preguntas como:
  - *"¿Cuántos clientes tenemos?"*
  - *"¿Qué coches BMW están disponibles?"*
  - *"¿Hay alquileres que vencen esta semana?"*
  - *"¿Cuántos agentes están activos?"*
- Los mensajes del usuario aparecen a la derecha (fondo cyan), las respuestas de Gemini a la izquierda (fondo oscuro)
- El botón "Limpiar" reinicia la conversación
- Si la API key no está configurada, muestra un mensaje de error claro
