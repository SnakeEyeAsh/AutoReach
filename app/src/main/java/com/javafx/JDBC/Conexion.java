package com.javafx.JDBC;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.javafx.Model.Alquiler;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.Agente;
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Conexion {

    private static HttpClient httpClient;
    private static String apiUrl;
    private static boolean initialized = false;

    // Sesión del agente autenticado
    private static String agenteToken   = null;
    private static int    agenteId      = -1;
    private static String agenteNombre  = "";

    // IDs de estados_vehiculo (se cargan al conectar)
    private static int ESTADO_DISPONIBLE_ID = 1;
    private static int ESTADO_ALQUILADO_ID  = 3;
    private static int ESTADO_VENDIDO_ID    = 2;

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Conexion() {}

    // ═══════════════════════════════════════════════════════════════
    // HELPERS HTTP
    // ═══════════════════════════════════════════════════════════════

    private static String enc(String val) {
        return URLEncoder.encode(val, StandardCharsets.UTF_8);
    }

    /** Builder base con Content-Type y, si hay sesión activa, Authorization. */
    private static HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/" + path))
                .header("Content-Type", "application/json");
        if (agenteToken != null && !agenteToken.isBlank())
            b.header("Authorization", "Bearer " + agenteToken);
        return b;
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return httpClient.send(baseRequest(path).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String json) throws Exception {
        return httpClient.send(
                baseRequest(path).POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    /** POST que devuelve el id generado del registro insertado */
    private static int postReturnId(String path, String json) throws Exception {
        HttpResponse<String> resp = post(path, json);
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            JsonElement el = JsonParser.parseString(resp.body());
            if (el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("id")) return obj.get("id").getAsInt();
            } else if (el.isJsonArray()) {
                JsonArray arr = el.getAsJsonArray();
                if (arr.size() > 0) return arr.get(0).getAsJsonObject().get("id").getAsInt();
            }
        }
        throw new Exception("Error al insertar (" + path + "): HTTP "
                + resp.statusCode() + " " + resp.body());
    }

    private static HttpResponse<String> put(String path, String json) throws Exception {
        return httpClient.send(
                baseRequest(path).PUT(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> delete(String path) throws Exception {
        return httpClient.send(baseRequest(path).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static JsonArray getArray(String path) throws Exception {
        HttpResponse<String> resp = get(path);
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            JsonElement el = JsonParser.parseString(resp.body());
            if (el.isJsonArray()) return el.getAsJsonArray();
        }
        System.err.println("HTTP " + resp.statusCode() + " en GET " + path + ": " + resp.body());
        return new JsonArray();
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS DE PARSEO JSON
    // ═══════════════════════════════════════════════════════════════

    private static String str(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        if (el == null || el.isJsonNull()) return "";
        return el.getAsString();
    }

    private static int intVal(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        if (el == null || el.isJsonNull()) return 0;
        return el.getAsInt();
    }

    private static double dblVal(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        if (el == null || el.isJsonNull()) return 0.0;
        return el.getAsDouble();
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS DE TIMESTAMP
    // ═══════════════════════════════════════════════════════════════

    private static Timestamp parseTimestamp(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            String cleaned = iso.replaceAll("\\+.*$|Z$", "").replaceAll("\\.\\d+$", "");
            if (cleaned.length() == 10) cleaned += "T00:00:00"; // solo fecha
            return Timestamp.valueOf(LocalDateTime.parse(cleaned, TS_FMT));
        } catch (DateTimeParseException e) {
            System.err.println("Error parseando timestamp: " + iso);
            return null;
        }
    }

    private static String fmtDate(Timestamp ts) {
        if (ts == null) return null;
        return ts.toLocalDateTime().format(DATE_FMT);
    }

    // ═══════════════════════════════════════════════════════════════
    // PARSE SAFE (compatibilidad con controladores existentes)
    // ═══════════════════════════════════════════════════════════════

    public static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Integer.parseInt(value.trim().replaceAll("[^0-9-]", "")); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    public static double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Double.parseDouble(value.trim().replaceAll("[^0-9.-]", "")); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    public static long parseLongSafe(String value, long defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Long.parseLong(value.trim().replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONEXIÓN
    // ═══════════════════════════════════════════════════════════════

    public static boolean isConexionActiva() {
        return initialized;
    }

    /** Mantenido por compatibilidad */
    public static Object getConnection() {
        return initialized ? httpClient : null;
    }

    public static boolean realizarConexion() {
        apiUrl = Config.get("api.url");

        if (apiUrl == null || apiUrl.isBlank()) {
            System.err.println("✗ api.url no encontrado en Application.properties");
            return false;
        }

        try {
            httpClient = HttpClient.newHttpClient();

            // Ping + cachear IDs de estados_vehiculo
            HttpResponse<String> resp = get("estados-vehiculo");
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                System.err.println("✗ Error al conectar con la API: HTTP " + resp.statusCode());
                return false;
            }

            JsonArray arr = JsonParser.parseString(resp.body()).getAsJsonArray();
            if (arr.size() == 0) {
                System.out.println("⚠ estados_vehiculo vacío — usando IDs por defecto (1=Disponible, 2=Vendido, 3=Alquilado)");
            } else {
                for (JsonElement el : arr) {
                    JsonObject obj = el.getAsJsonObject();
                    String desc = str(obj, "descripcion");
                    int id      = intVal(obj, "id");
                    switch (desc) {
                        case "Disponible" -> ESTADO_DISPONIBLE_ID = id;
                        case "Vendido"    -> ESTADO_VENDIDO_ID    = id;
                        case "Alquilado"  -> ESTADO_ALQUILADO_ID  = id;
                    }
                }
            }

            initialized = true;
            System.out.println("✓ Conexión API exitosa: " + apiUrl);
            System.out.println("  Estados → Disponible=" + ESTADO_DISPONIBLE_ID
                    + ", Alquilado=" + ESTADO_ALQUILADO_ID
                    + ", Vendido=" + ESTADO_VENDIDO_ID);
            return true;

        } catch (Exception e) {
            System.err.println("✗ ERROR al conectar con la API: " + e.getMessage());
            e.printStackTrace();
            initialized = false;
            return false;
        }
    }

    public static void cerrarConexion() {
        initialized = false;
        httpClient  = null;
        agenteToken  = null;
        agenteId     = -1;
        agenteNombre = "";
        System.out.println("Conexión API cerrada");
    }

    // ═══════════════════════════════════════════════════════════════
    // AUTENTICACIÓN DE AGENTES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Autentica un agente contra POST /api/agentes/login.
     * Almacena el token JWT y los datos del agente para usarlos en el resto de llamadas.
     * @throws Exception con el mensaje de error devuelto por la API ("Credenciales incorrectas",
     *                   "Cuenta desactivada", etc.)
     */
    public static void login(String username, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/api/agentes/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("[LOGIN] POST " + apiUrl + "/api/agentes/login");
        System.out.println("[LOGIN] Status: " + resp.statusCode());
        System.out.println("[LOGIN] Body: " + resp.body());

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            String mensaje = "Error de autenticación";
            try {
                JsonObject err = JsonParser.parseString(resp.body()).getAsJsonObject();
                if (err.has("error")) mensaje = err.get("error").getAsString();
            } catch (Exception ignored) {}
            throw new Exception(mensaje);
        }

        JsonObject data   = JsonParser.parseString(resp.body()).getAsJsonObject();
        agenteToken       = str(data, "token");
        JsonObject agente = data.getAsJsonObject("agente");
        agenteId          = intVal(agente, "id");
        agenteNombre      = str(agente, "nombre") + " " + str(agente, "apellidos");
        agenteNombre      = agenteNombre.trim();

        System.out.println("✓ Sesión iniciada: " + agenteNombre + " (id=" + agenteId + ")");
    }

    public static int    getAgenteId()     { return agenteId; }
    public static String getAgenteNombre() { return agenteNombre; }

    // ═══════════════════════════════════════════════════════════════
    // RESOLVERS INTERNOS
    // ═══════════════════════════════════════════════════════════════

    /** Devuelve el id (INT) de un cliente dado su dni_nif, o -1 si no existe */
    private static int resolverClienteId(String dniNif) throws Exception {
        JsonArray arr = getArray("clientes?search=" + enc(dniNif));
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (dniNif.equalsIgnoreCase(str(obj, "dni_nif"))) {
                return intVal(obj, "id");
            }
        }
        return -1;
    }

    /** Devuelve el VIN de un coche dado su matrícula, o null si no existe */
    private static String resolverVin(String matricula) throws Exception {
        JsonArray arr = getArray("coches");
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (matricula.equalsIgnoreCase(str(obj, "matricula"))) {
                return str(obj, "vin");
            }
        }
        return null;
    }

    /** Obtiene (o crea) una marca por nombre; devuelve su id */
    private static int obtenerOCrearMarca(String nombre) throws Exception {
        JsonArray arr = getArray("marcas");
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (nombre.equalsIgnoreCase(str(obj, "nombre"))) {
                return intVal(obj, "id");
            }
        }
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        return postReturnId("marcas", body.toString());
    }

    /** Obtiene (o crea) un modelo por nombre + marcaId; devuelve su id */
    private static int obtenerOCrearModelo(String nombre, int marcaId) throws Exception {
        JsonArray arr = getArray("modelos?marca_id=" + marcaId);
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (nombre.equalsIgnoreCase(str(obj, "nombre"))) {
                return intVal(obj, "id");
            }
        }
        JsonObject body = new JsonObject();
        body.addProperty("nombre",   nombre);
        body.addProperty("marca_id", marcaId);
        return postReturnId("modelos", body.toString());
    }

    /** Extrae [marca, modelo] del objeto coche con el shape de la API propia */
    private static String[] extractMarcaModelo(JsonObject obj) {
        String marca = "", modelo = "";
        JsonElement modEl = obj.get("modelo");
        if (modEl != null && !modEl.isJsonNull() && modEl.isJsonObject()) {
            JsonObject mod = modEl.getAsJsonObject();
            modelo = str(mod, "nombre");
            JsonElement marcaEl = mod.get("marca");
            if (marcaEl != null && !marcaEl.isJsonNull() && marcaEl.isJsonObject()) {
                marca = str(marcaEl.getAsJsonObject(), "nombre");
            }
        }
        return new String[]{marca, modelo};
    }

    /** Determina si el estado del coche es "Disponible" */
    private static boolean esDisponible(JsonObject obj) {
        JsonElement estEl = obj.get("estado");
        if (estEl != null && !estEl.isJsonNull() && estEl.isJsonObject()) {
            return intVal(estEl.getAsJsonObject(), "id") == ESTADO_DISPONIBLE_ID;
        }
        return intVal(obj, "estado_id") == ESTADO_DISPONIBLE_ID;
    }

    /**
     * Parsea detalles_interiores, que la API devuelve como string serializado JSON.
     * Devuelve [diseñoLlantas, diametroLlantas, diseñoInterior, materialInterior].
     */
    private static String[] parseDetalles(JsonObject obj) {
        String disenoL = "", disenoInt = "", material = "";
        int diametro = 0;
        JsonElement detEl = obj.get("detalles_interiores");
        if (detEl != null && !detEl.isJsonNull()) {
            String detStr = detEl.isJsonPrimitive() ? detEl.getAsString() : detEl.toString();
            try {
                JsonObject det = JsonParser.parseString(detStr).getAsJsonObject();
                disenoL   = str(det, "diseño_llantas");
                diametro  = intVal(det, "diametro_llantas");
                disenoInt = str(det, "diseño_interior");
                material  = str(det, "material_interior");
            } catch (Exception e) {
                disenoL = detStr; // texto plano: guardar tal cual
            }
        }
        return new String[]{disenoL, String.valueOf(diametro), disenoInt, material};
    }

    /** Construye un CocheCompleto desde un JsonObject de la API */
    private static CocheCompleto cocheCompletoFromJson(JsonObject obj) {
        String[] mm  = extractMarcaModelo(obj);
        boolean disp = esDisponible(obj);
        String[] det = parseDetalles(obj);

        return new CocheCompleto(
                str(obj, "vin"), mm[0], mm[1],
                intVal(obj, "anio"),
                dblVal(obj, "kilometraje"),
                disp, disp,
                dblVal(obj, "precio_venta_base"),
                str(obj, "color"),
                det[0], parseIntSafe(det[1], 0),
                det[2], det[3],
                str(obj, "matricula"));
    }

    // ═══════════════════════════════════════════════════════════════
    // CLIENTES
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<Cliente> mostrarClientes() {
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("clientes");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                long tel = parseLongSafe(str(obj, "telefono"), 0L);
                Cliente c = new Cliente(str(obj, "nombre"), str(obj, "apellidos"),
                        str(obj, "dni_nif"), tel, str(obj, "email"), str(obj, "direccion"));
                c.setId(intVal(obj, "id"));
                c.setFechaNacimiento(str(obj, "fecha_registro"));
                lista.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar clientes: " + e.getMessage());
        }
        return lista;
    }

    public static List<Cliente> buscarClientes(String dni) {
        List<Cliente> lista = new ArrayList<>();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("clientes?search=" + enc(dni));
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (!dni.equalsIgnoreCase(str(obj, "dni_nif"))) continue;
                long tel = parseLongSafe(str(obj, "telefono"), 0L);
                Cliente c = new Cliente(str(obj, "nombre"), str(obj, "apellidos"),
                        str(obj, "dni_nif"), tel, str(obj, "email"), str(obj, "direccion"));
                c.setId(intVal(obj, "id"));
                c.setFechaNacimiento(str(obj, "fecha_registro"));
                lista.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return lista;
    }

    public static void crearCliente(String nombre, String apellidos, String dni, String telefono,
            String email, String direccion, String fechaNac, String sexo) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("nombre",    nombre);
        body.addProperty("apellidos", apellidos);
        body.addProperty("dni_nif",   dni);
        body.addProperty("telefono",  telefono);
        body.addProperty("email",     email);
        body.addProperty("direccion", direccion);
        if (fechaNac != null && !fechaNac.isBlank()) body.addProperty("fecha_registro", fechaNac);

        HttpResponse<String> resp = post("clientes", body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear cliente: " + resp.body());
        }
        System.out.println("✓ Cliente creado");
    }

    public static void registrarAuth(String email) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        HttpResponse<String> resp = post("clientes/registrar-auth", body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al invitar cliente: " + resp.body());
        }
        System.out.println("✓ Invitación enviada a " + email);
    }

    public static void editarCliente(Cliente c) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre",    c.getNombre());
        body.addProperty("apellidos", c.getApellidos());
        body.addProperty("telefono",  String.valueOf(c.getTelefono()));
        body.addProperty("email",     c.getEmail());
        body.addProperty("direccion", c.getDireccion());

        try {
            HttpResponse<String> resp = put("clientes/" + c.getId(), body.toString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                System.out.println("✓ Cliente actualizado");
            } else {
                System.err.println("Error al editar cliente: " + resp.body());
            }
        } catch (Exception e) {
            System.err.println("Error al editar cliente: " + e.getMessage());
        }
    }

    public static void eliminarCliente(Cliente c) {
        try {
            HttpResponse<String> resp = delete("clientes/" + c.getId());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                System.out.println("✓ Cliente eliminado");
            } else {
                System.err.println("Error al eliminar cliente: " + resp.body());
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // COCHES - INVENTARIO
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<Coche> mostrarCoches() {
        ObservableList<Coche> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("coches");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (!esDisponible(obj)) continue;
                String[] mm  = extractMarcaModelo(obj);
                String[] det = parseDetalles(obj);

                lista.add(new Coche(
                        mm[0], mm[1], str(obj, "matricula"),
                        String.valueOf(intVal(obj, "anio")),
                        dblVal(obj, "kilometraje") + " Km",
                        str(obj, "vin"),
                        dblVal(obj, "precio_venta_base") + "",
                        det[0], det[1], str(obj, "color")));
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar coches: " + e.getMessage());
        }
        return lista;
    }

    public static List<String> obtenerMarcas() {
        List<String> lista = new ArrayList<>();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("marcas");
            for (JsonElement el : arr) {
                String nombre = str(el.getAsJsonObject(), "nombre");
                if (!nombre.isEmpty()) lista.add(nombre);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener marcas: " + e.getMessage());
        }
        return lista;
    }

    public static List<String> obtenerModelosPorMarca(String marca) {
        List<String> lista = new ArrayList<>();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            // Resolver marca_id filtrando client-side
            JsonArray marcaArr = getArray("marcas");
            int marcaId = -1;
            for (JsonElement el : marcaArr) {
                JsonObject obj = el.getAsJsonObject();
                if (marca.equalsIgnoreCase(str(obj, "nombre"))) {
                    marcaId = intVal(obj, "id");
                    break;
                }
            }
            if (marcaId == -1) return lista;

            JsonArray arr = getArray("modelos?marca_id=" + marcaId);
            for (JsonElement el : arr) {
                String nombre = str(el.getAsJsonObject(), "nombre");
                if (!nombre.isEmpty()) lista.add(nombre);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener modelos: " + e.getMessage());
        }
        return lista;
    }

    public static ObservableList<Disponible> mostrarCoincidencias(String marca) {
        ObservableList<Disponible> lista = FXCollections.observableArrayList();
        try {
            JsonArray arr = getArray("coches");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String[] mm = extractMarcaModelo(obj);
                if (!marca.equalsIgnoreCase(mm[0])) continue;
                boolean disponible = esDisponible(obj);
                lista.add(new Disponible(
                        String.valueOf(intVal(obj, "anio")), str(obj, "color"),
                        disponible ? "Si" : "No",
                        dblVal(obj, "precio_alquiler_dia") + "",
                        str(obj, "matricula")));
            }
        } catch (Exception e) {
            System.err.println("Error en mostrarCoincidencias(marca): " + e.getMessage());
        }
        return lista;
    }

    public static ObservableList<Disponible> mostrarCoincidencias(String marca, String modelo) {
        ObservableList<Disponible> lista = FXCollections.observableArrayList();
        try {
            JsonArray arr = getArray("coches");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String[] mm = extractMarcaModelo(obj);
                if (!marca.equalsIgnoreCase(mm[0])) continue;
                if (!modelo.equalsIgnoreCase(mm[1])) continue;
                boolean disponible = esDisponible(obj);
                lista.add(new Disponible(
                        String.valueOf(intVal(obj, "anio")), str(obj, "color"),
                        disponible ? "Si" : "No",
                        dblVal(obj, "precio_alquiler_dia") + "",
                        str(obj, "matricula")));
            }
        } catch (Exception e) {
            System.err.println("Error en mostrarCoincidencias(marca,modelo): " + e.getMessage());
        }
        return lista;
    }

    // ═══════════════════════════════════════════════════════════════
    // COCHES - COMPRA
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<CocheCompra> buscarCocheFlexible(
            String marca, String modelo, String color,
            String diseñoInterior, String materialInterior) {

        ObservableList<CocheCompra> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }

        try {
            JsonArray arr = getArray("coches/disponibles");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String[] mm  = extractMarcaModelo(obj);
                String[] det = parseDetalles(obj);

                if (marca != null && !marca.isBlank() && !marca.equals("Marca")
                        && !marca.equalsIgnoreCase(mm[0])) continue;
                if (modelo != null && !modelo.isBlank()
                        && !modelo.equalsIgnoreCase(mm[1])) continue;
                if (color != null && !color.isBlank() && !color.equals("Color")
                        && !color.equalsIgnoreCase(str(obj, "color"))) continue;

                lista.add(new CocheCompra(
                        mm[0], mm[1], String.valueOf(intVal(obj, "anio")),
                        dblVal(obj, "precio_venta_base") + "",
                        str(obj, "color"), det[0],
                        str(obj, "matricula"), det[1],
                        det[2], det[3]));
            }
        } catch (Exception e) {
            System.err.println("Error en buscarCocheFlexible: " + e.getMessage());
        }
        return lista;
    }

    /** Compatibilidad con el controlador de compra */
    public static ObservableList<CocheCompra> buscarCoche(CocheCompra coche) {
        if (coche == null) return FXCollections.observableArrayList();
        return buscarCocheFlexible(coche.getMarca(), coche.getModelo(),
                coche.getColor(), coche.getInterior(), coche.getMaterial());
    }

    // ═══════════════════════════════════════════════════════════════
    // COCHES - DISPONIBILIDAD
    // ═══════════════════════════════════════════════════════════════

    public static void actualizarAlquilado(String matricula) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }
            JsonObject body = new JsonObject();
            body.addProperty("estado_id", ESTADO_ALQUILADO_ID);
            put("coches/" + enc(vin), body.toString());
            System.out.println("✓ Coche marcado como alquilado");
        } catch (Exception e) {
            System.err.println("Error al actualizar estado alquilado: " + e.getMessage());
        }
    }

    public static void liberarCoche(String matricula) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }
            JsonObject body = new JsonObject();
            body.addProperty("estado_id", ESTADO_DISPONIBLE_ID);
            put("coches/" + enc(vin), body.toString());
            System.out.println("✓ Coche liberado");
        } catch (Exception e) {
            System.err.println("Error al liberar coche: " + e.getMessage());
        }
    }

    /** Marca un coche como vendido. La API ya lo hace automáticamente en POST /transacciones/venta,
     *  pero este método lo mantiene disponible para uso directo. */
    public static void marcarComoVendido(String matricula) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }
            JsonObject body = new JsonObject();
            body.addProperty("estado_id", ESTADO_VENDIDO_ID);
            HttpResponse<String> resp = put("coches/" + enc(vin), body.toString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300)
                System.out.println("✓ Coche marcado como vendido");
            else
                throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        } catch (Exception e) {
            System.err.println("Error al marcar como vendido: " + e.getMessage());
        }
    }

    /** Devuelve el id numérico del cliente con el DNI/NIF indicado, o -1 si no existe. */
    public static int obtenerClienteIdPorDni(String dni) {
        try {
            return resolverClienteId(dni);
        } catch (Exception e) {
            System.err.println("Error al obtener id de cliente: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registra una venta. La API cambia automáticamente el estado del coche a "Vendido".
     * @return el id generado de la transacción, o -1 si falla.
     */
    public static int registrarVenta(String matricula, int clienteId, double monto,
                                     String estadoPago) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) throw new Exception("Matrícula no encontrada: " + matricula);

            JsonObject body = new JsonObject();
            body.addProperty("vin_coche",  vin);
            body.addProperty("cliente_id", clienteId);
            body.addProperty("monto_total", monto);
            body.addProperty("estado_pago", estadoPago);
            if (agenteId > 0) body.addProperty("agente_id", agenteId);

            int id = postReturnId("transacciones/venta", body.toString());
            System.out.println("✓ Venta registrada con id=" + id);
            return id;
        } catch (Exception e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            return -1;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // COCHES - CRUD INGRESOS
    // ═══════════════════════════════════════════════════════════════

    public static void crearIngreso(
            String numBastidor, String marca, String modelo, String año, String kilometraje,
            boolean disponibleVentas, boolean disponibleAlquiler,
            String precioBase, String color, String diseñoLlantas, String diametroLlantas,
            String matricula) throws Exception {

        if (!isConexionActiva()) throw new Exception("Sin conexión activa");

        int marcaId  = obtenerOCrearMarca(marca);
        int modeloId = obtenerOCrearModelo(modelo, marcaId);
        int estadoId = (disponibleVentas || disponibleAlquiler) ? ESTADO_DISPONIBLE_ID : ESTADO_ALQUILADO_ID;

        JsonObject detalles = new JsonObject();
        detalles.addProperty("diseño_llantas",   diseñoLlantas);
        detalles.addProperty("diametro_llantas",  parseIntSafe(diametroLlantas, 0));

        JsonObject body = new JsonObject();
        body.addProperty("vin",                 numBastidor);
        body.addProperty("modelo_id",           modeloId);
        body.addProperty("estado_id",           estadoId);
        body.addProperty("anio",                parseIntSafe(año, 0));
        body.addProperty("kilometraje",         parseDoubleSafe(kilometraje, 0.0));
        body.addProperty("precio_venta_base",   parseDoubleSafe(precioBase, 0.0));
        body.addProperty("precio_alquiler_dia", parseDoubleSafe(precioBase, 0.0) / 100.0);
        body.addProperty("color",               color);
        body.addProperty("detalles_interiores", detalles.toString());
        body.addProperty("matricula",           matricula);

        HttpResponse<String> resp = post("coches", body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear ingreso: " + resp.body());
        }
        System.out.println("✓ Coche insertado");
    }

    public static CocheCompleto buscarIngreso(String numBastidor) {
        CocheCompleto defecto = new CocheCompleto(numBastidor, "", "", 0, 0.0,
                false, false, 0.0, "", "", 0, "", "", "");
        if (!isConexionActiva()) return defecto;
        try {
            HttpResponse<String> resp = get("coches/" + enc(numBastidor));
            if (resp.statusCode() == 404) return defecto;
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return cocheCompletoFromJson(JsonParser.parseString(resp.body()).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error al buscar ingreso: " + e.getMessage());
        }
        return defecto;
    }

    public static CocheCompleto buscarCochePorMatricula(String matricula) {
        if (!isConexionActiva()) return null;
        try {
            String vin = resolverVin(matricula);
            if (vin == null) return null;
            HttpResponse<String> resp = get("coches/" + enc(vin));
            if (resp.statusCode() == 404) return null;
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return cocheCompletoFromJson(JsonParser.parseString(resp.body()).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error al buscar coche por matrícula: " + e.getMessage());
        }
        return null;
    }

    public static void actualizarCoche(
            String numBastidor, String marca, String modelo, String año, String kilometraje,
            boolean disponibleVentas, boolean disponibleAlquiler,
            String precioBase, String color, String diseñoLlantas, String diametroLlantas,
            String matricula) throws Exception {

        if (!isConexionActiva()) throw new Exception("Sin conexión activa");

        int marcaId  = obtenerOCrearMarca(marca);
        int modeloId = obtenerOCrearModelo(modelo, marcaId);
        int estadoId = (disponibleVentas || disponibleAlquiler) ? ESTADO_DISPONIBLE_ID : ESTADO_ALQUILADO_ID;

        JsonObject detalles = new JsonObject();
        detalles.addProperty("diseño_llantas",  diseñoLlantas);
        detalles.addProperty("diametro_llantas", parseIntSafe(diametroLlantas, 0));

        JsonObject body = new JsonObject();
        body.addProperty("modelo_id",           modeloId);
        body.addProperty("estado_id",           estadoId);
        body.addProperty("anio",                parseIntSafe(año, 0));
        body.addProperty("kilometraje",         parseDoubleSafe(kilometraje, 0.0));
        body.addProperty("precio_venta_base",   parseDoubleSafe(precioBase, 0.0));
        body.addProperty("color",               color);
        body.addProperty("detalles_interiores", detalles.toString());
        body.addProperty("matricula",           matricula);

        HttpResponse<String> resp = put("coches/" + enc(numBastidor), body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al actualizar coche: " + resp.body());
        }
        System.out.println("✓ Coche actualizado");
    }

    public static void borrarCoche(String numBastidor) throws Exception {
        HttpResponse<String> resp = delete("coches/" + enc(numBastidor));
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al borrar coche: " + resp.body());
        }
        System.out.println("✓ Coche eliminado");
    }

    // ═══════════════════════════════════════════════════════════════
    // ALQUILERES
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<Alquiler> mostrarAlquiler() {
        ObservableList<Alquiler> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("transacciones?tipo=ALQUILER");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();

                // Excluir alquileres cancelados
                if ("Cancelado".equalsIgnoreCase(str(obj, "estado_pago"))) continue;

                // DNI del cliente
                String dni = "";
                JsonElement cliEl = obj.get("cliente");
                if (cliEl != null && !cliEl.isJsonNull() && cliEl.isJsonObject()) {
                    dni = str(cliEl.getAsJsonObject(), "dni_nif");
                } else {
                    JsonElement cidEl = obj.get("cliente_id");
                    if (cidEl != null && !cidEl.isJsonNull()) dni = cidEl.getAsString();
                }

                // Matrícula del coche
                String matricula = "";
                JsonElement cocEl = obj.get("coche");
                if (cocEl != null && !cocEl.isJsonNull() && cocEl.isJsonObject()) {
                    matricula = str(cocEl.getAsJsonObject(), "matricula");
                }

                // Fechas desde detalle_alquiler (objeto o array según respuesta)
                Timestamp fechaRecogida = null, fechaDevolucion = null;
                String hora = "";
                JsonElement detEl = obj.get("detalle_alquiler");
                if (detEl != null && !detEl.isJsonNull()) {
                    JsonObject det = null;
                    if (detEl.isJsonObject()) {
                        det = detEl.getAsJsonObject();
                    } else if (detEl.isJsonArray() && detEl.getAsJsonArray().size() > 0) {
                        det = detEl.getAsJsonArray().get(0).getAsJsonObject();
                    }
                    if (det != null) {
                        fechaRecogida   = parseTimestamp(str(det, "fecha_inicio"));
                        fechaDevolucion = parseTimestamp(str(det, "fecha_fin_prevista"));
                        if (fechaDevolucion != null) {
                            hora = fechaDevolucion.toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));
                        }
                    }
                }
                lista.add(new Alquiler(fechaDevolucion, hora, dni, matricula, fechaRecogida));
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar alquileres: " + e.getMessage());
        }
        return lista;
    }

    /** Crea un alquiler y devuelve el id de la transacción generada. */
    public static int crearAlquiler(
            String fecha, String fecha_fin, String precio_total, String precio_cuota,
            String estado, String empleado_id, String hora,
            String id_vehiculo, String cliente_id) throws Exception {

        int clienteIntId = resolverClienteId(cliente_id);
        if (clienteIntId == -1) throw new Exception("Cliente no encontrado con DNI: " + cliente_id);

        String vin = resolverVin(id_vehiculo);
        if (vin == null) throw new Exception("Vehículo no encontrado con matrícula: " + id_vehiculo);

        JsonObject body = new JsonObject();
        body.addProperty("vin_coche",  vin);
        body.addProperty("cliente_id", clienteIntId);
        body.addProperty("monto_total", parseDoubleSafe(precio_total, 0.0));
        body.addProperty("estado_pago", estado);
        if (agenteId > 0) body.addProperty("agente_id", agenteId);
        String fechaFinConHora = (hora != null && !hora.isBlank()) ? fecha_fin + "T" + hora + ":00" : fecha_fin;
        body.addProperty("fecha_inicio",       fecha);
        body.addProperty("fecha_fin_prevista", fechaFinConHora);
        body.addProperty("kilometraje_salida", 0);

        HttpResponse<String> resp = post("transacciones/alquiler", body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear alquiler: " + resp.body());
        }
        int transId = JsonParser.parseString(resp.body()).getAsJsonObject().get("id").getAsInt();
        System.out.println("✓ Alquiler creado id=" + transId);
        return transId;
    }

    public static void editarAlquiler(Timestamp fechaRecogida, String hora,
            Timestamp fechaEntrega, String matricula, String dni) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }

            int clienteId = resolverClienteId(dni);
            if (clienteId == -1) { System.err.println("Cliente no encontrado: " + dni); return; }

            // Buscar el id de la transacción de alquiler activa para este coche y cliente
            JsonArray tranArr = getArray("transacciones?tipo=ALQUILER&cliente_id=" + clienteId);
            int transId = -1;
            for (JsonElement el : tranArr) {
                JsonObject obj = el.getAsJsonObject();
                JsonElement cocEl = obj.get("coche");
                if (cocEl != null && !cocEl.isJsonNull() && cocEl.isJsonObject()) {
                    String vinTrans = str(cocEl.getAsJsonObject(), "vin");
                    if (vin.equals(vinTrans)) {
                        transId = intVal(obj, "id");
                        break;
                    }
                }
            }
            if (transId == -1) { System.err.println("Alquiler no encontrado"); return; }

            JsonObject body = new JsonObject();
            body.addProperty("fecha_inicio", fmtDate(fechaRecogida));
            String fechaFinConHora = (hora != null && !hora.isBlank())
                    ? fmtDate(fechaEntrega) + "T" + hora + ":00"
                    : fmtDate(fechaEntrega);
            body.addProperty("fecha_fin_prevista", fechaFinConHora);

            put("transacciones/" + transId + "/fechas", body.toString());
            System.out.println("✓ Alquiler editado");
        } catch (Exception e) {
            System.err.println("Error al editar alquiler: " + e.getMessage());
        }
    }

    /** Cancela un alquiler marcando su estado_pago como "Cancelado".
     *  Nota: la API no expone DELETE para transacciones. */
    public static void eliminarAlquiler(String matricula) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }

            JsonArray arr = getArray("transacciones?tipo=ALQUILER");
            int transId = -1;
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                JsonElement cocEl = obj.get("coche");
                if (cocEl != null && !cocEl.isJsonNull() && cocEl.isJsonObject()) {
                    if (vin.equals(str(cocEl.getAsJsonObject(), "vin"))) {
                        transId = intVal(obj, "id");
                        break;
                    }
                }
            }
            if (transId == -1) { System.err.println("Alquiler no encontrado para: " + matricula); return; }

            JsonObject body = new JsonObject();
            body.addProperty("estado_pago", "Cancelado");
            put("transacciones/" + transId + "/pago", body.toString());
            System.out.println("✓ Alquiler cancelado");
        } catch (Exception e) {
            System.err.println("Error al cancelar alquiler: " + e.getMessage());
        }
    }

    public static List<String> obtenerMatriculasPorCliente(String dniCliente) {
        List<String> matriculas = new ArrayList<>();
        if (!isConexionActiva() || dniCliente == null || dniCliente.isBlank()) return matriculas;
        try {
            int clienteId = resolverClienteId(dniCliente);
            if (clienteId == -1) return matriculas;

            JsonArray arr = getArray("transacciones?cliente_id=" + clienteId);
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                JsonElement cocEl = obj.get("coche");
                if (cocEl != null && !cocEl.isJsonNull() && cocEl.isJsonObject()) {
                    String mat = str(cocEl.getAsJsonObject(), "matricula");
                    if (!mat.isEmpty() && !matriculas.contains(mat)) matriculas.add(mat);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener matrículas del cliente: " + e.getMessage());
        }
        return matriculas;
    }

    // ═══════════════════════════════════════════════════════════════
    // DOCUMENTOS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Crea un documento asociado a una transacción.
     * @param clienteId     id del cliente
     * @param transaccionId id de la transacción
     * @param tipo          CONTRATO_VENTA | CONTRATO_ALQUILER | FACTURA | PRESUPUESTO
     * @param contenido     texto del documento
     * @return id del documento creado, o -1 si falla
     */
    public static int crearDocumento(int clienteId, int transaccionId,
                                     String tipo, String contenido) {
        if (!isConexionActiva()) return -1;
        try {
            JsonObject body = new JsonObject();
            body.addProperty("cliente_id",     clienteId);
            body.addProperty("transaccion_id", transaccionId);
            body.addProperty("tipo_documento", tipo);
            body.addProperty("contenido",      contenido);
            body.addProperty("estado_documento", "PENDIENTE_FIRMA");

            HttpResponse<String> resp = post("documentos", body.toString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                int id = JsonParser.parseString(resp.body()).getAsJsonObject().get("id").getAsInt();
                System.out.println("✓ Documento creado id=" + id);
                return id;
            }
            System.err.println("Error al crear documento: " + resp.statusCode() + " " + resp.body());
        } catch (Exception e) {
            System.err.println("Error al crear documento: " + e.getMessage());
        }
        return -1;
    }

    /** Devuelve documentos filtrados por tipo. Pasa null para no filtrar. */
    public static ObservableList<com.javafx.Model.Documento> obtenerDocumentos(
            String tipoDocumento, String estadoDocumento) {
        ObservableList<com.javafx.Model.Documento> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) return lista;
        try {
            StringBuilder path = new StringBuilder("documentos?");
            if (tipoDocumento  != null) path.append("tipo_documento=").append(enc(tipoDocumento)).append("&");
            if (estadoDocumento != null) path.append("estado_documento=").append(enc(estadoDocumento)).append("&");
            String url = path.toString().replaceAll("[&?]$", "");

            JsonArray arr = getArray(url);
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();

                int    id     = intVal(obj, "id");
                String tipo   = str(obj, "tipo_documento");
                String estado = str(obj, "estado_documento");
                String fecha  = str(obj, "fecha_creacion").replace("T", " ").replaceAll("\\..*$", "");
                String fFirma = str(obj, "fecha_firma");
                if (!fFirma.isEmpty()) fFirma = fFirma.replace("T", " ").replaceAll("\\..*$", "");
                String firmadoPor = str(obj, "firmado_por");

                String clienteNom = "";
                if (obj.has("cliente") && !obj.get("cliente").isJsonNull()) {
                    JsonObject c = obj.getAsJsonObject("cliente");
                    clienteNom = str(c, "nombre") + " " + str(c, "apellidos");
                }

                String transInfo = "";
                if (obj.has("transaccion") && !obj.get("transaccion").isJsonNull()) {
                    JsonObject t = obj.getAsJsonObject("transaccion");
                    transInfo = "#" + intVal(t, "id") + " — " + str(t, "tipo_transaccion")
                            + " — " + String.format("%.2f €", dblVal(t, "monto_total"));
                }

                lista.add(new com.javafx.Model.Documento(id, tipo, clienteNom.trim(),
                        transInfo, fecha, estado, fFirma, firmadoPor));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener documentos: " + e.getMessage());
        }
        return lista;
    }

    /** Firma un documento. */
    public static boolean firmarDocumento(int documentoId, String firmadoPor) {
        if (!isConexionActiva()) return false;
        try {
            JsonObject body = new JsonObject();
            body.addProperty("firmado_por", firmadoPor);
            HttpResponse<String> resp = put("documentos/" + documentoId + "/firmar", body.toString());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            System.err.println("Error al firmar documento: " + e.getMessage());
            return false;
        }
    }

    /** Cancelación lógica de un documento. */
    public static boolean cancelarDocumento(int documentoId) {
        if (!isConexionActiva()) return false;
        try {
            HttpResponse<String> resp = delete("documentos/" + documentoId);
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            System.err.println("Error al cancelar documento: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // TRANSACCIONES - ESTADO DE PAGO
    // ═══════════════════════════════════════════════════════════════

    /**
     * Devuelve todas las transacciones del tipo indicado ("VENTA" o "ALQUILER")
     * cuyo estado_pago sea "Pendiente".
     */
    public static ObservableList<com.javafx.Model.Transaccion> obtenerTransaccionesPendientes(String tipo) {
        ObservableList<com.javafx.Model.Transaccion> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) return lista;
        try {
            JsonArray arr = getArray("transacciones?tipo=" + tipo + "&estado_pago=Pendiente");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();

                int    id    = intVal(obj, "id");
                String fecha = str(obj, "fecha_transaccion").replace("T", " ").replaceAll("\\..*$", "");
                String monto = String.format("%.2f €", dblVal(obj, "monto_total"));
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

                lista.add(new com.javafx.Model.Transaccion(id, tipo, fecha, cliente.trim(), matricula, monto, estado));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener transacciones pendientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza el estado_pago de una transacción.
     * @return true si la operación fue exitosa
     */
    public static boolean actualizarEstadoPago(int transaccionId, String nuevoEstado) {
        if (!isConexionActiva()) return false;
        try {
            JsonObject body = new JsonObject();
            body.addProperty("estado_pago", nuevoEstado);
            HttpResponse<String> resp = put("transacciones/" + transaccionId + "/pago", body.toString());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            System.err.println("Error al actualizar estado de pago: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // AGENTES - CRUD
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<Agente> mostrarAgentes() {
        ObservableList<Agente> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("agentes");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
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
                a.setId(intVal(obj, "id"));
                lista.add(a);
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar agentes: " + e.getMessage());
        }
        return lista;
    }

    public static void crearAgente(String username, String password, String nombre,
            String apellidos, String telefono, String email) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username",  username);
        body.addProperty("password",  password);
        body.addProperty("nombre",    nombre);
        body.addProperty("apellidos", apellidos);
        body.addProperty("telefono",  telefono);
        body.addProperty("email",     email);

        System.out.println("[CREAR AGENTE] URL: " + apiUrl + "/api/agentes");
        System.out.println("[CREAR AGENTE] Body: " + body.toString());
        HttpResponse<String> resp = post("agentes", body.toString());
        System.out.println("[CREAR AGENTE] Status: " + resp.statusCode());
        System.out.println("[CREAR AGENTE] Response: " + resp.body());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear agente: " + resp.body());
        }
        System.out.println("✓ Agente creado");
    }

    public static void editarAgente(Agente a, String nuevaPassword) {
        JsonObject body = new JsonObject();
        body.addProperty("username",  a.getUsername());
        body.addProperty("nombre",    a.getNombre());
        body.addProperty("apellidos", a.getApellidos());
        body.addProperty("telefono",  a.getTelefono());
        body.addProperty("email",     a.getEmail());
        body.addProperty("activo",    a.isActivo());
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

    public static void eliminarAgente(int id) {
        try {
            HttpResponse<String> resp = delete("agentes/" + id);
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                System.out.println("✓ Agente eliminado");
            } else {
                System.err.println("Error al eliminar agente: " + resp.body());
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar agente: " + e.getMessage());
        }
    }
}
