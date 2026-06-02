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

        // Añadir mensaje del usuario al historial
        JsonObject turnoUsuario = new JsonObject();
        turnoUsuario.addProperty("role", "user");
        turnoUsuario.addProperty("content", mensajeUsuario);
        historial.add(turnoUsuario);

        // Construir messages: system + historial
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
        body.addProperty("max_tokens", 4096);

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

        // Añadir respuesta al historial
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
