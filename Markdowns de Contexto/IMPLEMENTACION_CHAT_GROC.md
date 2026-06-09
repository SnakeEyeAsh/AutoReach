# Implementación — Chat IA con Groq en AutoReach

**Proyecto:** AutoReach — app de escritorio JavaFX, gestión de alquiler/venta de vehículos  
**Stack:** JavaFX 21, JDK 17, Gradle 8, `java.net.http.HttpClient`, Gson  
**IA usada:** Groq API (modelo `llama-3.3-70b-versatile`) — gratuito, sin restricciones EU  
**Estado general: ✅ IMPLEMENTACIÓN COMPLETA** — todos los archivos ya están modificados

---

## Resumen de estado

| Archivo | Estado | Qué se hizo |
|---------|--------|-------------|
| `Application.properties` | ✅ Hecho | Propiedad `claude.api.key` con la key de Groq |
| `GeminiService.java` | ✅ Hecho | Groq API, modelo `llama-3.3-70b-versatile`, max_tokens=4096 |
| `cambiarColores.java` | ✅ Hecho | Sobrecarga de 6 botones |
| `pantalla1.fxml` | ✅ Hecho | Botón `btnChat` con `groq.png` + panel de chat completo |
| `controladorApp.java` | ✅ Hecho | Campos FXML, navegación, contexto completo con relaciones, bloqueo de input, auto-scroll, reglas de idioma |
| `Transaccion.java` | ✅ Hecho | Campo `agente` añadido con constructor compatible |
| `Conexion.java` | ✅ Hecho | `obtenerTodasLasTransacciones()` con parseo de agente |

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

## Mejoras y correcciones aplicadas — Estado actual

Todas las mejoras están ya aplicadas en el código. Este apartado documenta el código final de cada una.

---

### Mejora 1 — `max_tokens` subido a 4096
**Estado: ✅ Aplicado** — `GeminiService.java`

```java
body.addProperty("max_tokens", 4096);
```

---

### Mejora 2 — `Transaccion.java` con campo `agente`
**Estado: ✅ Aplicado** — `app/src/main/java/com/javafx/Model/Transaccion.java`

Se añadió el campo `agente` y un constructor de 8 parámetros manteniendo compatibilidad con el de 7:

```java
private final String agente;

// Constructor original (7 params) — sigue funcionando
public Transaccion(int id, String tipo, String fecha,
                   String cliente, String matricula,
                   String monto, String estadoPago) {
    this(id, tipo, fecha, cliente, matricula, monto, estadoPago, "");
}

// Constructor nuevo con agente (8 params)
public Transaccion(int id, String tipo, String fecha,
                   String cliente, String matricula,
                   String monto, String estadoPago, String agente) {
    // ...
    this.agente = agente;
}

public String getAgente() { return agente; }
```

---

### Mejora 3 — `obtenerTodasLasTransacciones()` en `Conexion.java`
**Estado: ✅ Aplicado** — `app/src/main/java/com/javafx/JDBC/Conexion.java`

Nuevo método que trae todas las transacciones (sin filtro de estado) e incluye el agente:

```java
public static ObservableList<com.javafx.Model.Transaccion> obtenerTodasLasTransacciones(String tipo) {
    ObservableList<com.javafx.Model.Transaccion> lista = FXCollections.observableArrayList();
    if (!isConexionActiva()) return lista;
    try {
        JsonArray arr = getArray("transacciones?tipo=" + tipo);
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            int    id     = intVal(obj, "id");
            String fecha  = str(obj, "fecha_transaccion").replace("T", " ").replaceAll("\\..*$", "");
            String monto  = String.format("%.2f €", dblVal(obj, "monto_total"));
            String estado = str(obj, "estado_pago");

            String cliente = "";
            if (obj.has("cliente") && !obj.get("cliente").isJsonNull()) {
                JsonObject c = obj.getAsJsonObject("cliente");
                cliente = str(c, "nombre") + " " + str(c, "apellidos");
            }
            String matricula = "";
            if (obj.has("coche") && !obj.get("coche").isJsonNull()) {
                matricula = str(obj.getAsJsonObject("coche"), "matricula");
            }

            // Parsear agente — puede venir como objeto o solo agente_id
            String agente = "";
            if (obj.has("agente") && !obj.get("agente").isJsonNull()) {
                JsonElement agenteEl = obj.get("agente");
                if (agenteEl.isJsonObject()) {
                    JsonObject a = agenteEl.getAsJsonObject();
                    agente = str(a, "nombre") + " " + str(a, "apellidos");
                    if (agente.isBlank()) agente = str(a, "username");
                }
            } else if (obj.has("agente_id") && !obj.get("agente_id").isJsonNull()) {
                agente = "ID:" + obj.get("agente_id").getAsString();
            }

            lista.add(new com.javafx.Model.Transaccion(id, tipo, fecha,
                    cliente.trim(), matricula, monto, estado, agente.trim()));
        }
    } catch (Exception e) {
        System.err.println("Error al obtener transacciones: " + e.getMessage());
    }
    return lista;
}
```

---

### Mejora 4 — `construirContextoGemini()` completo y final
**Estado: ✅ Aplicado** — `controladorApp.java`

Versión definitiva con: prompt de reglas estrictas, todos los campos de cada entidad, cruce de nombres de cliente en alquileres, transacciones completas con agente, y resumen estadístico:

```java
private String construirContextoGemini() {
    StringBuilder ctx = new StringBuilder();
    ctx.append("Eres el asistente inteligente de AutoReach, sistema de gestión de alquiler y venta de vehículos.\n");
    ctx.append("REGLAS OBLIGATORIAS — debes seguirlas siempre sin excepción:\n");
    ctx.append("1. Responde ÚNICAMENTE en español, sin importar el idioma del usuario.\n");
    ctx.append("2. Usa SOLO texto plano. Prohibido usar markdown: sin asteriscos, sin #, sin guiones de lista, sin negrita, sin cursiva.\n");
    ctx.append("3. Solo responde preguntas sobre AutoReach (clientes, coches, alquileres, agentes). Si preguntan otra cosa, di exactamente: 'Solo puedo ayudarte con información del sistema AutoReach.'\n");
    ctx.append("4. Usa los datos que te proporciono. No inventes información.\n");
    ctx.append("5. Para estadísticas o totales, calcula con los datos disponibles.\n");
    ctx.append("6. Los coches en 'COCHES DISPONIBLES' están libres ahora mismo.\n\n");

    try {
        // ── CLIENTES ──────────────────────────────────────────────
        javafx.collections.ObservableList<com.javafx.Model.Cliente> clientes =
                com.javafx.JDBC.Conexion.mostrarClientes();
        ctx.append("=== CLIENTES REGISTRADOS (").append(clientes.size()).append(") ===\n");
        for (com.javafx.Model.Cliente c : clientes) {
            ctx.append("ID:").append(c.getId())
               .append(" | ").append(c.getNombre()).append(" ").append(c.getApellidos())
               .append(" | DNI: ").append(c.getDni())
               .append(" | Tel: ").append(c.getTelefono())
               .append(" | Email: ").append(c.getEmail())
               .append(" | Dirección: ").append(c.getDireccion())
               .append("\n");
        }

        // Mapa DNI → nombre cliente para cruzar datos con alquileres
        java.util.Map<String, String> nombrePorDni = new java.util.HashMap<>();
        for (com.javafx.Model.Cliente c : clientes) {
            nombrePorDni.put(c.getDni(), c.getNombre() + " " + c.getApellidos());
        }

        // ── COCHES DISPONIBLES ────────────────────────────────────
        javafx.collections.ObservableList<com.javafx.Model.Coche> coches =
                com.javafx.JDBC.Conexion.mostrarCoches();
        ctx.append("\n=== COCHES DISPONIBLES (").append(coches.size()).append(") ===\n");
        for (com.javafx.Model.Coche c : coches) {
            ctx.append("Matrícula: ").append(c.getMatricula())
               .append(" | ").append(c.getMarca()).append(" ").append(c.getModelo())
               .append(" | Año: ").append(c.getAño())
               .append(" | Color: ").append(c.getColor())
               .append(" | Km: ").append(c.getKms())          // getter correcto: getKms()
               .append(" | Precio: ").append(c.getPrecio()).append("€")
               .append(" | Bastidor: ").append(c.getBastidor())
               .append("\n");
        }

        // ── ALQUILERES ACTIVOS (con nombre cliente cruzado) ───────
        javafx.collections.ObservableList<com.javafx.Model.Alquiler> alquileres =
                com.javafx.JDBC.Conexion.mostrarAlquiler();
        ctx.append("\n=== ALQUILERES ACTIVOS (").append(alquileres.size()).append(") ===\n");
        for (com.javafx.Model.Alquiler a : alquileres) {
            String nombreCliente = nombrePorDni.getOrDefault(a.getDni(), a.getDni());
            ctx.append("Matrícula: ").append(a.getMatricula())
               .append(" | Cliente: ").append(nombreCliente).append(" (DNI: ").append(a.getDni()).append(")")
               .append(" | Recogida: ").append(a.getFechaRecogida())
               .append(" | Devolución prevista: ").append(a.getFechaDevolucion())
               .append(" | Hora: ").append(a.getHoraDevolucion())
               .append("\n");
        }

        // ── AGENTES ───────────────────────────────────────────────
        javafx.collections.ObservableList<com.javafx.Model.Agente> agentes =
                com.javafx.JDBC.Conexion.mostrarAgentes();
        ctx.append("\n=== AGENTES DEL SISTEMA (").append(agentes.size()).append(") ===\n");
        for (com.javafx.Model.Agente a : agentes) {
            ctx.append("ID:").append(a.getId())
               .append(" | ").append(a.getNombre()).append(" ").append(a.getApellidos())
               .append(" | Usuario: ").append(a.getUsername())
               .append(" | Email: ").append(a.getEmail())
               .append(" | Tel: ").append(a.getTelefono())
               .append(" | Activo: ").append(a.isActivo() ? "Sí" : "No")
               .append("\n");
        }

        // ── TRANSACCIONES DE VENTA ────────────────────────────────
        javafx.collections.ObservableList<com.javafx.Model.Transaccion> ventas =
                com.javafx.JDBC.Conexion.obtenerTodasLasTransacciones("VENTA");
        ctx.append("\n=== TRANSACCIONES DE VENTA (").append(ventas.size()).append(") ===\n");
        for (com.javafx.Model.Transaccion t : ventas) {
            ctx.append("ID:").append(t.getId())
               .append(" | Matrícula: ").append(t.getMatricula())
               .append(" | Cliente: ").append(t.getCliente())
               .append(" | Agente: ").append(t.getAgente().isBlank() ? "Sin asignar" : t.getAgente())
               .append(" | Monto: ").append(t.getMonto())
               .append(" | Estado pago: ").append(t.getEstadoPago())
               .append(" | Fecha: ").append(t.getFecha())
               .append("\n");
        }

        // ── TRANSACCIONES DE ALQUILER ─────────────────────────────
        javafx.collections.ObservableList<com.javafx.Model.Transaccion> transAlquileres =
                com.javafx.JDBC.Conexion.obtenerTodasLasTransacciones("ALQUILER");
        ctx.append("\n=== TRANSACCIONES DE ALQUILER (").append(transAlquileres.size()).append(") ===\n");
        for (com.javafx.Model.Transaccion t : transAlquileres) {
            ctx.append("ID:").append(t.getId())
               .append(" | Matrícula: ").append(t.getMatricula())
               .append(" | Cliente: ").append(t.getCliente())
               .append(" | Agente: ").append(t.getAgente().isBlank() ? "Sin asignar" : t.getAgente())
               .append(" | Monto: ").append(t.getMonto())
               .append(" | Estado pago: ").append(t.getEstadoPago())
               .append(" | Fecha: ").append(t.getFecha())
               .append("\n");
        }

        // ── RESUMEN ESTADÍSTICO ───────────────────────────────────
        ctx.append("\n=== RESUMEN ESTADÍSTICO ===\n");
        ctx.append("Total clientes: ").append(clientes.size()).append("\n");
        ctx.append("Coches disponibles ahora: ").append(coches.size()).append("\n");
        ctx.append("Alquileres activos: ").append(alquileres.size()).append("\n");
        ctx.append("Ventas registradas: ").append(ventas.size()).append("\n");
        ctx.append("Transacciones de alquiler registradas: ").append(transAlquileres.size()).append("\n");
        ctx.append("Agentes activos: ")
           .append(agentes.stream().filter(com.javafx.Model.Agente::isActivo).count())
           .append(" de ").append(agentes.size()).append(" totales\n");

    } catch (Exception e) {
        ctx.append("[Error al cargar datos: ").append(e.getMessage()).append("]");
    }

    return ctx.toString();
}
```

> **Nota importante:** el getter de kilómetros en `Coche.java` es `getKms()`, NO `getKilometraje()`. Usar el incorrecto causa error de compilación.

---

### Notas de implementación

- Groq con `llama-3.3-70b-versatile` soporta hasta **128k tokens** de contexto — margen suficiente para toda la BD
- Si la BD crece mucho (>500 registros) considerar enviar solo los últimos N registros por fecha
- El método `obtenerTodasLasTransacciones()` reemplaza a `obtenerTransaccionesPendientes()` en el contexto — el segundo solo traía registros con estado "Pendiente"

---

## Notas importantes

- **La key de Groq** se guarda en la propiedad `claude.api.key` de `Application.properties` (nombre heredado, no cambia el funcionamiento)
- **`GeminiService.java`** se llama así por nombre histórico, pero internamente usa Groq
- **Groq free tier:** 14.400 peticiones/día, 30 req/minuto, sin tarjeta, sin restricciones EU
- El chat mantiene **historial de conversación** durante la sesión — puede hacer preguntas de seguimiento
- Al navegar al panel chat se cargan los datos de la BD en un hilo de fondo antes de responder

---

## Correcciones de comportamiento aplicadas

### Fix 1 — Bloquear input hasta que el contexto esté cargado

**Problema:** Si el usuario enviaba un mensaje antes de que el hilo de fondo terminase de construir el contexto, el modelo recibía un system prompt vacío y respondía en inglés con markdown sin restricciones.

**Archivo:** `controladorApp.java` — método `irAChat()`  
**Qué cambiar:** Deshabilitar `txtMensajeChat` y `btnEnviarChat` al abrir el panel y habilitarlos solo cuando el contexto esté listo.

```java
// Bloquear input hasta que el contexto esté listo
txtMensajeChat.setDisable(true);
btnEnviarChat.setDisable(true);
lblEstadoChat.setText("Cargando datos...");

new Thread(() -> {
    String contexto = construirContextoGemini();
    geminiService.setContexto(contexto);
    javafx.application.Platform.runLater(() -> {
        lblEstadoChat.setText("✓ Datos cargados");
        txtMensajeChat.setDisable(false);
        btnEnviarChat.setDisable(false);
        txtMensajeChat.requestFocus();
    });
}).start();
```

---

### Fix 2 — Prompt de sistema con reglas estrictas

**Problema:** El modelo respondía en inglés y usaba formato markdown (`**negrita**`, `# títulos`) que JavaFX `Label` no renderiza, mostrando los asteriscos en crudo.

**Archivo:** `controladorApp.java` — inicio de `construirContextoGemini()`  
**Qué cambiar:** Reemplazar las líneas iniciales del StringBuilder por reglas numeradas explícitas:

```java
ctx.append("Eres el asistente inteligente de AutoReach, sistema de gestión de alquiler y venta de vehículos.\n");
ctx.append("REGLAS OBLIGATORIAS — debes seguirlas siempre sin excepción:\n");
ctx.append("1. Responde ÚNICAMENTE en español, sin importar el idioma del usuario.\n");
ctx.append("2. Usa SOLO texto plano. Prohibido usar markdown: sin asteriscos, sin #, sin guiones de lista, sin negrita, sin cursiva.\n");
ctx.append("3. Solo responde preguntas sobre AutoReach (clientes, coches, alquileres, agentes). Si preguntan otra cosa, di exactamente: 'Solo puedo ayudarte con información del sistema AutoReach.'\n");
ctx.append("4. Usa los datos que te proporciono. No inventes información.\n");
ctx.append("5. Para estadísticas o totales, calcula con los datos disponibles.\n");
ctx.append("6. Los coches en 'COCHES DISPONIBLES' están libres ahora mismo.\n\n");
```

---

### Fix 3 — Auto-scroll fiable con listener en altura del VBox

**Problema:** El `Platform.runLater(() -> scrollChat.setVvalue(1.0))` dentro de `agregarMensajeChat()` no era fiable porque se ejecutaba antes de que el layout terminase de renderizar el nuevo mensaje.

**Archivo:** `controladorApp.java` — método `irAChat()`  
**Qué cambiar:** Añadir un `ChangeListener` en `vboxMensajes.heightProperty()` que se registra una sola vez mediante un flag booleano:

```java
private boolean chatScrollListenerActivo = false;

// Dentro de irAChat(), antes del hilo de carga:
if (!chatScrollListenerActivo) {
    vboxMensajes.heightProperty().addListener((obs, oldVal, newVal) ->
        javafx.application.Platform.runLater(() -> scrollChat.setVvalue(1.0)));
    chatScrollListenerActivo = true;
}
```

> El listener se dispara cada vez que el VBox crece (nuevo mensaje añadido) y garantiza el scroll al fondo tras el layout completo.
