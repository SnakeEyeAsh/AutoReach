# Implementación — Chat IA con Groq en AutoReach

**Proyecto:** AutoReach — app de escritorio JavaFX, gestión de alquiler/venta de vehículos  
**Stack:** JavaFX 21, JDK 17, Gradle 8, `java.net.http.HttpClient`, Gson  
**IA usada:** Groq API (modelo `llama-3.3-70b-versatile`) — gratuito, sin restricciones EU  
**Estado general: ✅ IMPLEMENTACIÓN COMPLETA** — todos los archivos ya están modificados

---

## Resumen de estado

| Archivo | Estado | Qué se hizo |
|---------|--------|-------------|
| `Application.properties` | ✅ Hecho | Añadida propiedad `claude.api.key` con la key de Groq |
| `GeminiService.java` | ✅ Hecho | Clase creada usando Groq API, modelo llama-3.3-70b |
| `cambiarColores.java` | ✅ Hecho | Añadida sobrecarga de 6 botones |
| `pantalla1.fxml` | ✅ Hecho | Botón `btnChat` en sidebar con `groq.png` + panel completo |
| `controladorApp.java` | ✅ Hecho | Campos FXML, métodos de chat, contexto BD, navegación |

---

## Archivos y código final (para referencia de DeepSeek)

### `Application.properties`
**Ruta:** `app/src/main/resources/Application.properties`

```properties
###############################################
#  API REST PROPIA — CONCESIONARIA
###############################################

api.url=http://35.202.97.203:3000

###############################################
#  GROQ IA
###############################################

claude.api.key=gsk_...tu_key_de_groq_aqui
```

> La key de Groq se obtiene gratis en https://console.groq.com — sin tarjeta, sin restricciones EU.  
> El nombre de la propiedad es `claude.api.key` por compatibilidad con el código existente.

---

### `GeminiService.java`
**Ruta:** `app/src/main/java/com/javafx/Service/GeminiService.java`  
**Estado: ✅ Archivo creado y funcional**

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
        this.apiKey = Config.get("claude.api.key");
    }

    public void setContexto(String contexto) {
        this.contextoSistema = contexto;
        historial.clear();
    }

    public String enviarMensaje(String mensajeUsuario) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("Clave de API no configurada en Application.properties (claude.api.key)");
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

        System.out.println("[GROQ] Status: " + response.statusCode());
        System.out.println("[GROQ] Response: " + response.body());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Error Groq API: HTTP " + response.statusCode()
                    + " — " + response.body());
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

        System.out.println("[GROQ] Pregunta: " + mensajeUsuario);
        System.out.println("[GROQ] Respuesta: " + textoRespuesta);

        return textoRespuesta;
    }

    public void limpiarHistorial() {
        historial.clear();
    }
}
```

---

### `cambiarColores.java`
**Ruta:** `app/src/main/java/com/javafx/App/cambiarColores.java`  
**Estado: ✅ Sobrecarga de 6 parámetros añadida**

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

### `pantalla1.fxml` — Botón sidebar
**Estado: ✅ Añadido después del botón `btnAgentes`**

```xml
<Button fx:id="btnChat" contentDisplay="TOP" maxHeight="Infinity" maxWidth="Infinity"
        mnemonicParsing="false" onAction="#irAChat" prefWidth="110.0"
        style="-fx-background-color: transparent; -fx-text-fill: #b0bcd4;
               -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-padding: 10 0;"
        text="IA Chat" VBox.vgrow="ALWAYS">
   <tooltip><Tooltip text="Chat con Asistente IA — Groq" /></tooltip>
   <graphic>
      <ImageView fitHeight="36.0" fitWidth="36.0" pickOnBounds="true" preserveRatio="true">
         <image><Image url="@Assets/groq.png" /></image>
      </ImageView>
   </graphic>
</Button>
```

> El archivo `groq.png` ya está en `app/src/main/resources/Assets/groq.png`.

---

### `pantalla1.fxml` — Panel de chat
**Estado: ✅ Añadido después del panel `panelAgentes`**

```xml
<Pane fx:id="panelChat" prefHeight="578.0" prefWidth="724.0" visible="false"
      style="-fx-background-color: #f0f4f8;">
   <children>
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

### `controladorApp.java`
**Estado: ✅ Todos los cambios aplicados**

#### Campos FXML añadidos (junto a panelAgentes)
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

#### Import añadido
```java
import com.javafx.Service.GeminiService;
```

#### Campo de instancia (junto a validadorAgente)
```java
private final GeminiService geminiService = new GeminiService();
```

#### `ocultarTodos()` — línea añadida
```java
panelChat.setVisible(false);
```

#### `desactivarBotones()` — línea añadida
```java
btnChat.setDisable(true);
```

#### `IniciarSesion()` — línea añadida tras login exitoso
```java
btnChat.setDisable(false);
```

#### Todas las llamadas `cambiarColores.cambiar()` — actualizadas a 6 parámetros
```java
cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso, btnAgentes, btnChat);
cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnAgentes, btnChat);
cambiarColores.cambiar(btnAgentes, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnChat);
```

#### Métodos del chat añadidos
```java
@FXML
void irAChat(ActionEvent event) {
    mostrarPanel(panelChat);
    cambiarColores.cambiar(btnChat, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes);
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
    javafx.application.Platform.runLater(() -> scrollChat.setVvalue(1.0));
}

private String construirContextoGemini() {
    StringBuilder ctx = new StringBuilder();
    ctx.append("Eres el asistente inteligente de AutoReach, sistema de gestión de ");
    ctx.append("alquiler y venta de vehículos. ");
    ctx.append("Responde siempre en español, de forma concisa y precisa. ");
    ctx.append("Solo responde sobre datos de AutoReach.\n\n");
    try {
        var clientes = com.javafx.JDBC.Conexion.mostrarClientes();
        ctx.append("=== CLIENTES: ").append(clientes.size()).append(" registrados.\n");
        for (var c : clientes) {
            ctx.append("- ").append(c.getNombre()).append(" ").append(c.getApellidos())
               .append(" | DNI: ").append(c.getDni())
               .append(" | Email: ").append(c.getEmail()).append("\n");
        }
        var coches = com.javafx.JDBC.Conexion.mostrarCoches();
        ctx.append("\n=== COCHES DISPONIBLES: ").append(coches.size()).append(" unidades.\n");
        for (var c : coches) {
            ctx.append("- ").append(c.getMarca()).append(" ").append(c.getModelo())
               .append(" (").append(c.getAño()).append(") | Matrícula: ").append(c.getMatricula()).append("\n");
        }
        var alquileres = com.javafx.JDBC.Conexion.mostrarAlquiler();
        ctx.append("\n=== ALQUILERES ACTIVOS: ").append(alquileres.size()).append("\n");
        for (var a : alquileres) {
            ctx.append("- Matrícula: ").append(a.getMatricula())
               .append(" | Cliente DNI: ").append(a.getDni()).append("\n");
        }
        var agentes = com.javafx.JDBC.Conexion.mostrarAgentes();
        ctx.append("\n=== AGENTES: ").append(agentes.size()).append("\n");
        for (var a : agentes) {
            ctx.append("- ").append(a.getNombre()).append(" ").append(a.getApellidos())
               .append(" | Usuario: ").append(a.getUsername())
               .append(" | Activo: ").append(a.isActivo() ? "Sí" : "No").append("\n");
        }
    } catch (Exception e) {
        ctx.append("[Error al cargar datos: ").append(e.getMessage()).append("]");
    }
    return ctx.toString();
}
```

---

## Notas importantes

- **La key de Groq** se guarda en la propiedad `claude.api.key` de `Application.properties` (nombre heredado, no cambia el funcionamiento)
- **`GeminiService.java`** se llama así por nombre histórico, pero internamente usa Groq
- **Groq free tier:** 14.400 peticiones/día, 30 req/minuto, sin tarjeta, sin restricciones EU
- El chat mantiene **historial de conversación** durante la sesión — puede hacer preguntas de seguimiento
- Al navegar al panel chat se cargan los datos de la BD en un hilo de fondo antes de responder
