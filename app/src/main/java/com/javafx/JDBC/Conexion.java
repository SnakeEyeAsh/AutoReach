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
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Conexion {

    private static HttpClient httpClient;
    private static String baseUrl;
    private static String apiKey;
    private static boolean initialized = false;

    // IDs de estados_vehiculo (se cargan al conectar)
    private static int ESTADO_DISPONIBLE_ID = 1;
    private static int ESTADO_ALQUILADO_ID  = 3;
    private static int ESTADO_VENDIDO_ID    = 2;

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Conexion() {}

    // ═══════════════════════════════════════════════════════════════
    // HELPERS HTTP
    // ═══════════════════════════════════════════════════════════════

    private static String enc(String val) {
        return URLEncoder.encode(val, StandardCharsets.UTF_8);
    }

    private static HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    /** POST que devuelve el id generado del registro insertado */
    private static int postReturnId(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            JsonArray arr = JsonParser.parseString(resp.body()).getAsJsonArray();
            if (arr.size() > 0) return arr.get(0).getAsJsonObject().get("id").getAsInt();
        }
        throw new Exception("Error al insertar (" + path + "): HTTP "
                + resp.statusCode() + " " + resp.body());
    }

    private static HttpResponse<String> patch(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .DELETE()
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static JsonArray getArray(String path) throws Exception {
        HttpResponse<String> resp = get(path);
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonParser.parseString(resp.body()).getAsJsonArray();
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
            return Timestamp.valueOf(LocalDateTime.parse(cleaned, TS_FMT));
        } catch (DateTimeParseException e) {
            System.err.println("Error parseando timestamp: " + iso);
            return null;
        }
    }

    private static String fmtTimestamp(Timestamp ts) {
        if (ts == null) return null;
        return ts.toLocalDateTime().format(TS_FMT);
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

    /** Mantenido por compatibilidad: devuelve httpClient si hay conexión, null si no */
    public static Object getConnection() {
        return initialized ? httpClient : null;
    }

    public static boolean realizarConexion() {
        baseUrl = Config.get("supabase.url");
        apiKey  = Config.get("supabase.key");

        if (baseUrl == null || apiKey == null) {
            System.err.println("✗ supabase.url o supabase.key no encontrados en Application.properties");
            return false;
        }

        try {
            httpClient = HttpClient.newHttpClient();

            // Ping + cachear IDs de estados_vehiculo
            HttpResponse<String> resp = get("estados_vehiculo?select=id,descripcion&order=id");
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                System.err.println("✗ Error al conectar con Supabase: HTTP " + resp.statusCode());
                return false;
            }

            JsonArray arr = JsonParser.parseString(resp.body()).getAsJsonArray();
            if (arr.size() == 0) {
                System.err.println("✗ La tabla estados_vehiculo está vacía. Inserta los estados base en Supabase:");
                System.err.println("  INSERT INTO estados_vehiculo (descripcion) VALUES ('Disponible'),('Vendido'),('Alquilado'),('Mantenimiento');");
                return false;
            }

            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String desc = str(obj, "descripcion");
                int id      = intVal(obj, "id");
                switch (desc) {
                    case "Disponible"  -> ESTADO_DISPONIBLE_ID = id;
                    case "Vendido"     -> ESTADO_VENDIDO_ID    = id;
                    case "Alquilado"   -> ESTADO_ALQUILADO_ID  = id;
                }
            }

            initialized = true;
            System.out.println("✓ Conexión Supabase REST API exitosa");
            System.out.println("  Estados → Disponible=" + ESTADO_DISPONIBLE_ID
                    + ", Alquilado=" + ESTADO_ALQUILADO_ID
                    + ", Vendido=" + ESTADO_VENDIDO_ID);
            return true;

        } catch (Exception e) {
            System.err.println("✗ ERROR al conectar con Supabase: " + e.getMessage());
            e.printStackTrace();
            initialized = false;
            return false;
        }
    }

    public static void cerrarConexion() {
        initialized = false;
        httpClient  = null;
        System.out.println("Conexión Supabase cerrada");
    }

    // ═══════════════════════════════════════════════════════════════
    // RESOLVERS INTERNOS
    // ═══════════════════════════════════════════════════════════════

    /** Devuelve el id (INT) de un cliente dado su dni_nif, o -1 si no existe */
    private static int resolverClienteId(String dniNif) throws Exception {
        JsonArray arr = getArray("clientes?dni_nif=eq." + enc(dniNif) + "&select=id");
        if (arr.size() > 0) return arr.get(0).getAsJsonObject().get("id").getAsInt();
        return -1;
    }

    /** Devuelve el VIN de un coche dado su matrícula, o null si no existe */
    private static String resolverVin(String matricula) throws Exception {
        JsonArray arr = getArray("coches?matricula=eq." + enc(matricula) + "&select=vin");
        if (arr.size() > 0) return str(arr.get(0).getAsJsonObject(), "vin");
        return null;
    }

    /** Obtiene (o crea) una marca por nombre; devuelve su id */
    private static int obtenerOCrearMarca(String nombre) throws Exception {
        JsonArray arr = getArray("marcas?nombre=eq." + enc(nombre) + "&select=id");
        if (arr.size() > 0) return arr.get(0).getAsJsonObject().get("id").getAsInt();
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        return postReturnId("marcas", body.toString());
    }

    /** Obtiene (o crea) un modelo por nombre + marcaId; devuelve su id */
    private static int obtenerOCrearModelo(String nombre, int marcaId) throws Exception {
        JsonArray arr = getArray("modelos?nombre=eq." + enc(nombre)
                + "&marca_id=eq." + marcaId + "&select=id");
        if (arr.size() > 0) return arr.get(0).getAsJsonObject().get("id").getAsInt();
        JsonObject body = new JsonObject();
        body.addProperty("nombre",   nombre);
        body.addProperty("marca_id", marcaId);
        return postReturnId("modelos", body.toString());
    }

    /** Extrae [marca, modelo] de un JsonObject con modelos(marcas) embebido */
    private static String[] extractMarcaModelo(JsonObject obj) {
        String marca = "", modelo = "";
        JsonElement modEl = obj.get("modelos");
        if (modEl != null && !modEl.isJsonNull()) {
            JsonObject mod = modEl.getAsJsonObject();
            modelo = str(mod, "nombre");
            JsonElement marcaEl = mod.get("marcas");
            if (marcaEl != null && !marcaEl.isJsonNull()) {
                marca = str(marcaEl.getAsJsonObject(), "nombre");
            }
        }
        return new String[]{marca, modelo};
    }

    /** Construye un CocheCompleto desde un JsonObject con relaciones embebidas */
    private static CocheCompleto cocheCompletoFromJson(JsonObject obj) {
        String[] mm    = extractMarcaModelo(obj);
        boolean dispon = intVal(obj, "estado_id") == ESTADO_DISPONIBLE_ID;

        String disenoL = "", disenoInt = "", material = "";
        int diametro   = 0;
        JsonElement detEl = obj.get("detalles_interiores");
        if (detEl != null && !detEl.isJsonNull()) {
            JsonObject det = detEl.getAsJsonObject();
            disenoL   = str(det, "diseño_llantas");
            diametro  = intVal(det, "diametro_llantas");
            disenoInt = str(det, "diseño_interior");
            material  = str(det, "material_interior");
        }

        return new CocheCompleto(
                str(obj, "vin"), mm[0], mm[1],
                intVal(obj, "anio"),
                dblVal(obj, "kilometraje"),
                dispon, dispon,
                dblVal(obj, "precio_venta_base"),
                str(obj, "color"),
                disenoL, diametro,
                disenoInt, material,
                str(obj, "matricula"));
    }

    // ═══════════════════════════════════════════════════════════════
    // CLIENTES
    // ═══════════════════════════════════════════════════════════════

    public static ObservableList<Cliente> mostrarClientes() {
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        if (!isConexionActiva()) { System.err.println("Sin conexión activa"); return lista; }
        try {
            JsonArray arr = getArray("clientes?select=nombre,apellidos,dni_nif,telefono,email,direccion&order=apellidos");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                long tel = parseLongSafe(str(obj, "telefono"), 0L);
                lista.add(new Cliente(str(obj, "nombre"), str(obj, "apellidos"),
                        str(obj, "dni_nif"), tel, str(obj, "email"), str(obj, "direccion")));
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
            JsonArray arr = getArray("clientes?dni_nif=eq." + enc(dni)
                    + "&select=nombre,apellidos,dni_nif,telefono,email,direccion");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                long tel = parseLongSafe(str(obj, "telefono"), 0L);
                lista.add(new Cliente(str(obj, "nombre"), str(obj, "apellidos"),
                        str(obj, "dni_nif"), tel, str(obj, "email"), str(obj, "direccion")));
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

        HttpResponse<String> resp = post("clientes", body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear cliente: " + resp.body());
        }
        System.out.println("✓ Cliente creado");
    }

    public static void editarCliente(Cliente c) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre",    c.getNombre());
        body.addProperty("apellidos", c.getApellidos());
        body.addProperty("telefono",  String.valueOf(c.getTelefono()));
        body.addProperty("email",     c.getEmail());
        body.addProperty("direccion", c.getDireccion());

        try {
            HttpResponse<String> resp = patch("clientes?dni_nif=eq." + enc(c.getDni()), body.toString());
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
            HttpResponse<String> resp = delete("clientes?dni_nif=eq." + enc(c.getDni()));
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
            JsonArray arr = getArray("coches?select=vin,anio,kilometraje,precio_venta_base,"
                    + "color,matricula,detalles_interiores,modelos(nombre,marcas(nombre))");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String[] mm = extractMarcaModelo(obj);

                String diseño = "", diametro = "";
                JsonElement detEl = obj.get("detalles_interiores");
                if (detEl != null && !detEl.isJsonNull()) {
                    JsonObject det = detEl.getAsJsonObject();
                    diseño = str(det, "diseño_llantas");
                    JsonElement dEl = det.get("diametro_llantas");
                    if (dEl != null && !dEl.isJsonNull()) diametro = dEl.getAsString();
                }

                lista.add(new Coche(
                        mm[0], mm[1], str(obj, "matricula"),
                        str(obj, "anio"),
                        dblVal(obj, "kilometraje") + " Km",
                        str(obj, "vin"),
                        dblVal(obj, "precio_venta_base") + "",
                        diseño, diametro, str(obj, "color")));
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
            JsonArray arr = getArray("marcas?select=nombre&order=nombre");
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
            JsonArray marcaArr = getArray("marcas?nombre=eq." + enc(marca) + "&select=id");
            if (marcaArr.size() == 0) return lista;
            int marcaId = marcaArr.get(0).getAsJsonObject().get("id").getAsInt();

            JsonArray arr = getArray("modelos?marca_id=eq." + marcaId + "&select=nombre&order=nombre");
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
            JsonArray marcaArr = getArray("marcas?nombre=eq." + enc(marca) + "&select=id");
            if (marcaArr.size() == 0) return lista;
            int marcaId = marcaArr.get(0).getAsJsonObject().get("id").getAsInt();

            JsonArray modArr = getArray("modelos?marca_id=eq." + marcaId + "&select=id");
            if (modArr.size() == 0) return lista;
            StringBuilder ids = new StringBuilder();
            for (JsonElement el : modArr) {
                if (ids.length() > 0) ids.append(",");
                ids.append(el.getAsJsonObject().get("id").getAsInt());
            }

            JsonArray arr = getArray("coches?modelo_id=in.(" + ids
                    + ")&select=anio,precio_alquiler_dia,color,matricula,estado_id");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                boolean disponible = intVal(obj, "estado_id") == ESTADO_DISPONIBLE_ID;
                lista.add(new Disponible(
                        str(obj, "anio"), str(obj, "color"),
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
            JsonArray marcaArr = getArray("marcas?nombre=eq." + enc(marca) + "&select=id");
            if (marcaArr.size() == 0) return lista;
            int marcaId = marcaArr.get(0).getAsJsonObject().get("id").getAsInt();

            JsonArray modArr = getArray("modelos?marca_id=eq." + marcaId
                    + "&nombre=eq." + enc(modelo) + "&select=id");
            if (modArr.size() == 0) return lista;
            int modeloId = modArr.get(0).getAsJsonObject().get("id").getAsInt();

            JsonArray arr = getArray("coches?modelo_id=eq." + modeloId
                    + "&select=anio,precio_alquiler_dia,color,matricula,estado_id");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                boolean disponible = intVal(obj, "estado_id") == ESTADO_DISPONIBLE_ID;
                lista.add(new Disponible(
                        str(obj, "anio"), str(obj, "color"),
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
            StringBuilder query = new StringBuilder(
                    "coches?estado_id=eq." + ESTADO_DISPONIBLE_ID
                    + "&select=vin,anio,precio_venta_base,color,matricula,"
                    + "detalles_interiores,modelos(nombre,marcas(nombre))");

            if (marca != null && !marca.isBlank() && !marca.equals("Marca")) {
                JsonArray marcaArr = getArray("marcas?nombre=eq." + enc(marca) + "&select=id");
                if (marcaArr.size() == 0) return lista;
                int marcaId = marcaArr.get(0).getAsJsonObject().get("id").getAsInt();

                if (modelo != null && !modelo.isBlank()) {
                    JsonArray modArr = getArray("modelos?marca_id=eq." + marcaId
                            + "&nombre=eq." + enc(modelo) + "&select=id");
                    if (modArr.size() == 0) return lista;
                    int modeloId = modArr.get(0).getAsJsonObject().get("id").getAsInt();
                    query.append("&modelo_id=eq.").append(modeloId);
                } else {
                    JsonArray modArr = getArray("modelos?marca_id=eq." + marcaId + "&select=id");
                    if (modArr.size() == 0) return lista;
                    StringBuilder ids = new StringBuilder();
                    for (JsonElement el : modArr) {
                        if (ids.length() > 0) ids.append(",");
                        ids.append(el.getAsJsonObject().get("id").getAsInt());
                    }
                    query.append("&modelo_id=in.(").append(ids).append(")");
                }
            }

            if (color != null && !color.isBlank() && !color.equals("Color")) {
                query.append("&color=eq.").append(enc(color));
            }

            JsonArray arr = getArray(query.toString());
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String[] mm = extractMarcaModelo(obj);

                String diseñoR = "", diametroL = "", interior = "", material = "";
                JsonElement detEl = obj.get("detalles_interiores");
                if (detEl != null && !detEl.isJsonNull()) {
                    JsonObject det = detEl.getAsJsonObject();
                    diseñoR  = str(det, "diseño_llantas");
                    JsonElement dEl = det.get("diametro_llantas");
                    if (dEl != null && !dEl.isJsonNull()) diametroL = dEl.getAsString();
                    interior = str(det, "diseño_interior");
                    material = str(det, "material_interior");
                }

                lista.add(new CocheCompra(
                        mm[0], mm[1], str(obj, "anio"),
                        dblVal(obj, "precio_venta_base") + "",
                        str(obj, "color"), diseñoR,
                        str(obj, "matricula"), diametroL,
                        interior, material));
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
            patch("coches?vin=eq." + enc(vin), body.toString());
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
            patch("coches?vin=eq." + enc(vin), body.toString());
            System.out.println("✓ Coche liberado");
        } catch (Exception e) {
            System.err.println("Error al liberar coche: " + e.getMessage());
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
        body.addProperty("vin",                numBastidor);
        body.addProperty("modelo_id",          modeloId);
        body.addProperty("estado_id",          estadoId);
        body.addProperty("anio",               parseIntSafe(año, 0));
        body.addProperty("kilometraje",        parseDoubleSafe(kilometraje, 0.0));
        body.addProperty("precio_venta_base",  parseDoubleSafe(precioBase, 0.0));
        body.addProperty("precio_alquiler_dia", parseDoubleSafe(precioBase, 0.0) / 100.0);
        body.addProperty("color",              color);
        body.add("detalles_interiores",        detalles);
        body.addProperty("matricula",          matricula);

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
            JsonArray arr = getArray("coches?vin=eq." + enc(numBastidor)
                    + "&select=*,modelos(nombre,marcas(nombre)),estados_vehiculo(descripcion)");
            if (arr.size() == 0) return defecto;
            return cocheCompletoFromJson(arr.get(0).getAsJsonObject());
        } catch (Exception e) {
            System.err.println("Error al buscar ingreso: " + e.getMessage());
            return defecto;
        }
    }

    public static CocheCompleto buscarCochePorMatricula(String matricula) {
        if (!isConexionActiva()) return null;
        try {
            JsonArray arr = getArray("coches?matricula=eq." + enc(matricula)
                    + "&select=*,modelos(nombre,marcas(nombre)),estados_vehiculo(descripcion)");
            if (arr.size() == 0) return null;
            return cocheCompletoFromJson(arr.get(0).getAsJsonObject());
        } catch (Exception e) {
            System.err.println("Error al buscar coche por matrícula: " + e.getMessage());
            return null;
        }
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
        body.addProperty("modelo_id",         modeloId);
        body.addProperty("estado_id",         estadoId);
        body.addProperty("anio",              parseIntSafe(año, 0));
        body.addProperty("kilometraje",       parseDoubleSafe(kilometraje, 0.0));
        body.addProperty("precio_venta_base", parseDoubleSafe(precioBase, 0.0));
        body.addProperty("color",             color);
        body.add("detalles_interiores",       detalles);
        body.addProperty("matricula",         matricula);

        HttpResponse<String> resp = patch("coches?vin=eq." + enc(numBastidor), body.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al actualizar coche: " + resp.body());
        }
        System.out.println("✓ Coche actualizado");
    }

    public static void borrarCoche(String numBastidor) throws Exception {
        HttpResponse<String> resp = delete("coches?vin=eq." + enc(numBastidor));
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
            JsonArray arr = getArray("transacciones?tipo_transaccion=eq.ALQUILER"
                    + "&select=id,clientes(dni_nif),coches(matricula),"
                    + "detalle_alquiler(fecha_inicio,fecha_fin_prevista)");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();

                String dni = "";
                JsonElement cliEl = obj.get("clientes");
                if (cliEl != null && !cliEl.isJsonNull()) dni = str(cliEl.getAsJsonObject(), "dni_nif");

                String matricula = "";
                JsonElement cocEl = obj.get("coches");
                if (cocEl != null && !cocEl.isJsonNull()) matricula = str(cocEl.getAsJsonObject(), "matricula");

                Timestamp fechaRecogida = null, fechaDevolucion = null;
                String hora = "";
                JsonElement detEl = obj.get("detalle_alquiler");
                if (detEl != null && !detEl.isJsonNull() && detEl.isJsonArray()) {
                    JsonArray dets = detEl.getAsJsonArray();
                    if (dets.size() > 0) {
                        JsonObject det = dets.get(0).getAsJsonObject();
                        fechaRecogida   = parseTimestamp(str(det, "fecha_inicio"));
                        fechaDevolucion = parseTimestamp(str(det, "fecha_fin_prevista"));
                        if (fechaRecogida != null) {
                            hora = fechaRecogida.toLocalDateTime()
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

    public static void crearAlquiler(
            String fecha, String fecha_fin, String precio_total, String precio_cuota,
            String estado, String empleado_id, String hora,
            String id_vehiculo, String cliente_id) throws Exception {

        // 1. Resolver cliente_id (INT) desde DNI
        int clienteIntId = resolverClienteId(cliente_id);
        if (clienteIntId == -1) throw new Exception("Cliente no encontrado con DNI: " + cliente_id);

        // 2. Resolver VIN desde matrícula
        String vin = resolverVin(id_vehiculo);
        if (vin == null) throw new Exception("Vehículo no encontrado con matrícula: " + id_vehiculo);

        // 3. Insertar transaccion y obtener su id
        JsonObject bodyTrans = new JsonObject();
        bodyTrans.addProperty("tipo_transaccion", "ALQUILER");
        bodyTrans.addProperty("vin_coche",        vin);
        bodyTrans.addProperty("cliente_id",       clienteIntId);
        bodyTrans.addProperty("empleado_id",      parseIntSafe(empleado_id, 1));
        bodyTrans.addProperty("monto_total",      parseDoubleSafe(precio_total, 0.0));
        bodyTrans.addProperty("estado_pago",      estado);

        int transaccionId = postReturnId("transacciones", bodyTrans.toString());

        // 4. Insertar detalle_alquiler
        JsonObject bodyDetalle = new JsonObject();
        bodyDetalle.addProperty("transaccion_id",     transaccionId);
        bodyDetalle.addProperty("fecha_inicio",       fecha + "T" + hora + ":00");
        bodyDetalle.addProperty("fecha_fin_prevista", fecha_fin + "T00:00:00");
        bodyDetalle.addProperty("fianza_depositada",  0);

        HttpResponse<String> resp = post("detalle_alquiler", bodyDetalle.toString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Error al crear detalle_alquiler: " + resp.body());
        }
        System.out.println("✓ Alquiler creado (transaccion #" + transaccionId + ")");
    }

    public static void editarAlquiler(Timestamp fechaRecogida, String hora,
            Timestamp fechaEntrega, String matricula, String dni) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }

            int clienteId = resolverClienteId(dni);
            if (clienteId == -1) { System.err.println("Cliente no encontrado: " + dni); return; }

            JsonArray tranArr = getArray("transacciones?vin_coche=eq." + enc(vin)
                    + "&cliente_id=eq." + clienteId
                    + "&tipo_transaccion=eq.ALQUILER&select=id");
            if (tranArr.size() == 0) { System.err.println("Alquiler no encontrado"); return; }
            int transId = tranArr.get(0).getAsJsonObject().get("id").getAsInt();

            JsonObject body = new JsonObject();
            body.addProperty("fecha_inicio",       fmtTimestamp(fechaRecogida));
            body.addProperty("fecha_fin_prevista", fmtTimestamp(fechaEntrega));

            patch("detalle_alquiler?transaccion_id=eq." + transId, body.toString());
            System.out.println("✓ Alquiler editado");
        } catch (Exception e) {
            System.err.println("Error al editar alquiler: " + e.getMessage());
        }
    }

    public static void eliminarAlquiler(String matricula) {
        try {
            String vin = resolverVin(matricula);
            if (vin == null) { System.err.println("Matrícula no encontrada: " + matricula); return; }
            // ON DELETE CASCADE elimina automáticamente detalle_alquiler
            delete("transacciones?vin_coche=eq." + enc(vin) + "&tipo_transaccion=eq.ALQUILER");
            System.out.println("✓ Alquiler eliminado");
        } catch (Exception e) {
            System.err.println("Error al eliminar alquiler: " + e.getMessage());
        }
    }

    public static List<String> obtenerMatriculasPorCliente(String dniCliente) {
        List<String> matriculas = new ArrayList<>();
        if (!isConexionActiva() || dniCliente == null || dniCliente.isBlank()) return matriculas;
        try {
            int clienteId = resolverClienteId(dniCliente);
            if (clienteId == -1) return matriculas;

            JsonArray arr = getArray("transacciones?cliente_id=eq." + clienteId + "&select=coches(matricula)");
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                JsonElement cocEl = obj.get("coches");
                if (cocEl != null && !cocEl.isJsonNull()) {
                    String mat = str(cocEl.getAsJsonObject(), "matricula");
                    if (!mat.isEmpty() && !matriculas.contains(mat)) matriculas.add(mat);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener matrículas del cliente: " + e.getMessage());
        }
        return matriculas;
    }
}
