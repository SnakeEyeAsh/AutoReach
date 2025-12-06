package com.javafx.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.core.SqlCommand;

import com.javafx.Model.Alquiler;
import com.javafx.Model.Cita;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//prueba de conexion a la base de datos
public class Conexion {

    private static Connection conexion;
    private static String url;
    private static String user;
    private static String password;

    public Conexion() {

    }

    protected static Connection getConnection() {
        return conexion;
    }

    public static void realizarConexion() {
        url = Config.get("db.url");
        user = Config.get("db.user");
        password = Config.get("db.password");

        try {
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("conexion realizada");

        } catch (SQLException e) {
            System.out.println(e.getMessage() + "el problemas es la conexion");
        }
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static ObservableList<Cliente> mostrarClientes() {

        ObservableList<Cliente> lista = FXCollections.observableArrayList();

        try {
            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT * FROM cliente;");
            while (resultado.next()) {
                String nombre = resultado.getString("nombre");
                String apellidos = resultado.getString("apellidos");
                String dni = resultado.getString("dni_nif");
                String telefono = resultado.getString("telefono");
                String mail = resultado.getString("email");
                String direccion = resultado.getString("direccion");

                // o como obtengas el valor
                long valor;
                if (telefono != null && !telefono.trim().isEmpty()) {
                    valor = Long.parseLong(telefono.trim());
                } else {
                    valor = 0L; // valor por defecto
                }

                Cliente c = new Cliente(nombre, apellidos, dni, valor, mail, direccion);

                lista.add(c);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    public static ObservableList<Cita> mostrarCitas() {

        ObservableList<Cita> lista = FXCollections.observableArrayList();

        try {
            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT * FROM cita;");
            while (resultado.next()) {
                String num_bastidor = resultado.getString("matricula");
                String fecha = resultado.getString("fecha");
                String hora = resultado.getString("hora");

                Cita c = new Cita(num_bastidor, fecha, hora);

                lista.add(c);
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta de citas " + error.getMessage());
        }
        return lista;

    }

    public static ObservableList<Alquiler> mostrarAlquiler() {

        ObservableList<Alquiler> lista = FXCollections.observableArrayList();

        try {

            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT * FROM transaccion WHERE fecha_fin IS NOT NULL;");
            while (resultado.next()) {

                Timestamp fecha = resultado.getTimestamp("fecha_fin");
                String hora = resultado.getString("hora");
                String dni = resultado.getString("cliente_dni");
                String matricula = resultado.getString("id_vehiculo");
                Timestamp fechaRecogida = resultado.getTimestamp("fecha");

                Alquiler a = new Alquiler(fecha, hora, dni, matricula, fechaRecogida);

                // System.out.println(matricula);

                lista.add(a);
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta de Alquiler " + error.getMessage());
        }
        return lista;

    }

    public static ObservableList<Coche> mostrarCoches() {

        ObservableList<Coche> lista = FXCollections.observableArrayList();

        try {
            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT * FROM coche;");
            while (resultado.next()) {

                String marca = resultado.getString("marca");
                String modelo = resultado.getString("modelo");
                Integer año = resultado.getInt("año");
                Float kms = resultado.getFloat("kilometraje");
                String bastidor = resultado.getString("num_bastidor");
                Double precio = resultado.getDouble("precio_base");
                String diseño = resultado.getString("diseño_llantas");
                String diseñoLlantas = resultado.getString("diametro_llantas");
                String color = resultado.getString("color");
                String matricula = resultado.getString("matricula");

                Coche c = new Coche(marca, modelo, matricula, año + "", kms + " Km", bastidor, precio + "", diseño,
                        diseñoLlantas, color);

                lista.add(c);
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta muestra de Coches " + error.getMessage());
        }
        return lista;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 7
    //////////////////////////////////////// CLIENTES////////////////////////////////////////////////////////////////////
    /// / ///////

    public static List<Cliente> buscarClientes(String dni) {

        String sql = "SELECT * FROM cliente WHERE dni_nif = ?;";

        List<Cliente> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            ResultSet resultado = ps.executeQuery();

            while (resultado.next()) {

                String nombre = resultado.getString("nombre");
                String apellido = resultado.getString("apellidos");
                String telefono = resultado.getString("telefono");
                String email = resultado.getString("email");
                String direccion = resultado.getString("direccion");

                Cliente c = new Cliente(nombre, apellido, dni, Double.parseDouble(telefono), email, direccion);
                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar Lista de clientes " + e.getMessage());
        }

        return lista;

    }

    public static void crearCliente(String nombre, String apellidos, String dni, String telefono, String email,
            String direccion, String fechaNac, String sexo) throws SQLException {

        String sql = "INSERT INTO cliente(nombre,apellidos,dni_nif,telefono,email,direccion,fecha_nac,sexo) VALUES(?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, dni);
            ps.setString(4, telefono);
            ps.setString(5, email);
            ps.setString(6, direccion);
            ps.setString(7, fechaNac);
            ps.setString(8, sexo);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al incertar usuario " + error.getMessage());

        }
    }

    public static void editarCliente(Cliente c) {

        String sql = "UPDATE cliente SET nombre = ?, apellidos = ?, telefono = ?, email = ?, direccion = ? WHERE dni_nif = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono() + "");
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getDni());

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al Modificar Cliente " + error.getMessage());

        }
    }

    public static void eliminarCliente(Cliente c) {

        String sql = "DELETE FROM cliente WHERE dni_nif = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, c.getDni());

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al Borrar cliente " + error.getMessage());

        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// 
    /// CITAS
    /// 
    /// ///////////////////////////////////////////////////////////////////////////
    public static void crearCita(String fecha, String hora, String informe, String matricula) throws SQLException {

        String sql = "INSERT INTO cita(fecha,hora,informe,matricula) VALUES(?,?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, fecha);
            ps.setString(2, hora);
            ps.setString(3, informe);
            ps.setString(4, matricula);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al incertar cita " + error.getMessage());

        }

    }
    ////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////--ALQUILER
    public static void crearAlquiler(
            String fecha,
            String fecha_fin,
            String precio_total,
            String precio_cuota,
            String estado,
            String empleado_id,
            String hora,
            String id_vehiculo,
            String cliente_id) throws SQLException {

        // System.out.println(fecha + fecha_fin);

        String sql = "INSERT INTO transaccion(fecha,fecha_fin,precio_total,precio_cuota,deposito,estado,empleado_id,hora,id_vehiculo,cliente_dni) VALUES(?,?,?,?,?,?,?,?,?,?)";

        /// SOLUCION TEMPORAL EL ESTADO DEBE SER PENDIENTE EN PROCESO O FINALIZADA

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fecha + " " + hora + ":00"));
            ps.setTimestamp(2, Timestamp.valueOf(fecha_fin + " 00:00:00"));
            ps.setDouble(3, Double.parseDouble(precio_total));
            ps.setDouble(4, Double.parseDouble(precio_cuota));
            ps.setDouble(5, 0);
            ps.setString(6, estado);
            ps.setInt(7, Integer.parseInt(empleado_id));
            ps.setString(8, hora);
            ps.setString(9, id_vehiculo);
            ps.setString(10, cliente_id);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al incertar Alquiler " + error.getMessage());

        }

    }

    public static void actualizarAlquilado(String matricula) {

        String sql = "UPDATE coche SET disponible_alquiler = false, disponible_ventas = false WHERE matricula = ?;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, matricula);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);
        } catch (SQLException e) {
            System.out.println("Error al cambiar la disponibilidad del coche " + e.getMessage());
        }
    }

    public static void liberarCoche(String matricula) {

        String sql = "UPDATE coche SET disponible_alquiler = true, disponible_ventas = true WHERE matricula = ?;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, matricula);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas al liberar coche: " + filas);
        } catch (SQLException e) {
            System.out.println("Error al liberar la disponibilidad del coche " + e.getMessage());
        }
    }

    public static List<String> obtenerMarcas() {

        List<String> lista = new ArrayList<>();
        try {

            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT DISTINCT marca FROM coche;");

            while (resultado.next()) {
                String marca = resultado.getString("marca");

                lista.add(marca);
                // System.out.println(marca);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public static List<String> obtenerModelosPorMarca(String marca) {

        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT modelo FROM coche WHERE marca = ? ORDER BY modelo;";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, marca);
            ResultSet resultado = ps.executeQuery();

            while (resultado.next()) {
                String modelo = resultado.getString("modelo");
                lista.add(modelo);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener modelos: " + e.getMessage());
        }

        return lista;
    }

    public static ObservableList<Disponible> mostrarCoincidencias(String marca) {

        ObservableList<Disponible> lista = FXCollections.observableArrayList();

        String sql = "SELECT * FROM coche WHERE Marca = ?";

        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, marca);

            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {

                    Integer año = resultado.getInt("año");
                    Double precio = resultado.getDouble("precio_base");
                    Boolean disponible = resultado.getBoolean("disponible_alquiler");
                    String color = resultado.getString("color");
                    String dis = disponible ? "Si" : "No";
                    String matricula = resultado.getString("matricula");

                    double precioAlq = (precio / 1000) * 2;

                    Disponible d = new Disponible(
                            año.toString(),
                            color,
                            dis,
                            precioAlq + "",
                            matricula);

                    lista.add(d);
                }
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta muestra de Coches: " + error.getMessage());
            error.printStackTrace(); // Útil para debugging
        }

        return lista;
    }

    public static ObservableList<Disponible> mostrarCoincidencias(String marca, String modelo) {

        ObservableList<Disponible> lista = FXCollections.observableArrayList();

        String sql = "SELECT * FROM coche WHERE marca = ? AND modelo = ?";

        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, marca);
            consulta.setString(2, modelo);

            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {

                    Integer año = resultado.getInt("año");
                    Double precio = resultado.getDouble("precio_base");
                    Boolean disponible = resultado.getBoolean("disponible_alquiler");
                    String color = resultado.getString("color");
                    String dis = disponible ? "Si" : "No";
                    String matricula = resultado.getString("matricula");

                    double precioAlq = (precio / 1000) * 2;

                    Disponible d = new Disponible(
                            año.toString(),
                            color,
                            dis,
                            precioAlq + "",
                            matricula);

                    lista.add(d);
                }
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta muestra de Coches por marca y modelo: " + error.getMessage());
            error.printStackTrace();
        }

        return lista;
    }

    /*
     * public static void actualizarDisponibles(boolean pestaña,String matricula){
     * 
     * String ventana = pestaña?"disponible_alquiler":"disponible_venta";
     * 
     * String sql =
     * "UPDATE coche SET "+ventana+" = false WHERE matricula='"+matricula+"';";
     * }
     */

    public static void editarAlquiler(Timestamp fechaRecogida, String Hora, Timestamp fechaEntrega, String matricula,
            String dni) {

        String sql = "UPDATE transaccion SET fecha = ?, hora = ?, fecha_fin = ? WHERE id_vehiculo = ? AND cliente_dni = ?;";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setTimestamp(1, fechaRecogida);
            ps.setString(2, Hora);
            ps.setTimestamp(3, fechaEntrega);
            ps.setString(4, matricula);
            ps.setString(5, dni);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException error) {

            System.out.println("Error al modificar Alquiler " + error.getMessage());

        }

    }

    public static void eliminarAlquiler(String Matricula) {

        String sql = "DELETE FROM transaccion WHERE id_vehiculo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, Matricula);

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas " + filas);

        } catch (SQLException e) {
            System.out.println("error al eliminar el alquiler " + e.getMessage());
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////COMPRA////////////////////////////////////////////////////////////////////////////////////////////
    /// 
    /// 
    public static ObservableList<CocheCompra> buscarCoche(CocheCompra coche) {

        ObservableList<CocheCompra> lista = FXCollections.observableArrayList();

        String sql = "SELECT * FROM coche WHERE marca = ? AND modelo = ? AND año = ? " +
                "AND diseño_interior = ? AND material_interior = ? AND color = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            // Establecer parámetros
            pst.setString(1, coche.getMarca());
            pst.setString(2, coche.getModelo());
            pst.setInt(3, Integer.parseInt(coche.getAño()));
            pst.setString(4, coche.getInterior());
            pst.setString(5, coche.getMaterial());
            pst.setString(6, coche.getColor());

            ResultSet resultado = pst.executeQuery();

            while (resultado.next()) {
                String marca = resultado.getString("marca");
                String modelo = resultado.getString("modelo");
                String año = resultado.getString("año");
                String precio = resultado.getString("precio_base");
                String color = resultado.getString("color");
                String diseñoRuedas = resultado.getString("diseño_llantas");
                String diametroLlantas = resultado.getString("diametro_llantas");
                String interior = resultado.getString("diseño_interior");
                String material = resultado.getString("material_interior");

                // ✅ Orden de parámetros corregido según el constructor
                CocheCompra c = new CocheCompra(marca, modelo, año, precio, color,
                        diseñoRuedas, material, diametroLlantas,
                        interior, material);

                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar coche: " + e.getMessage());
            e.printStackTrace();
        }
        // System.out.println(lista);
        return lista;
    }

    public static void realizarCompra() {

    }

    ////////////////////////////////////////////////////// /////////////////////////
    /// /////////////////////////////////////////////////////////////////////////////
    /// //////////////////////////////////// ///
    /// /////////////////////////////------------INGRESOS DE
    /// AUTOS----------------////////////////////////////////////////////////////////
    /// /// //
    /// / ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void crearIngreso(
            String numBastidor,
            String marca,
            String modelo,
            String año,
            String kilometraje,
            boolean disponibleVentas,
            boolean disponibleAlquiler,
            String precioBase,
            String color,
            String diseñoLlantas,
            String diametroLlantas,
            String diseñoInterior,
            String materialInterior,
            String matricula) throws SQLException {

        String sql = "INSERT INTO coche (num_bastidor, marca, modelo, año, kilometraje, disponible_ventas, disponible_alquiler, precio_base, color, diseño_llantas, diametro_llantas, diseño_interior, material_interior, matricula) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, numBastidor);
            ps.setString(2, marca);
            ps.setString(3, modelo);
            ps.setInt(4, Integer.parseInt(año));
            ps.setDouble(5, Double.parseDouble(kilometraje));
            ps.setBoolean(6, disponibleVentas);
            ps.setBoolean(7, disponibleAlquiler);
            ps.setDouble(8, Double.parseDouble(precioBase));
            ps.setString(9, color);
            ps.setString(10, diseñoLlantas);
            ps.setInt(11, Integer.parseInt(diametroLlantas));
            ps.setString(12, diseñoInterior);
            ps.setString(13, materialInterior);
            ps.setString(14, matricula);

            int filas = ps.executeUpdate();
            System.out.println("Filas insertadas: " + filas);

        } catch (SQLException error) {
            System.out.println("Error al insertar Ingreso: " + error.getMessage());
        }
    }

    // Buscar
    public static CocheCompleto buscarIngreso(String num_bastidor) {

        CocheCompleto c = new CocheCompleto(num_bastidor, num_bastidor, num_bastidor, null, null, null, null, null,
                num_bastidor, num_bastidor, null, num_bastidor, num_bastidor, num_bastidor);

        String sql = "SELECT * FROM coche WHERE num_bastidor = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, num_bastidor);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                c.setNumBastidor(num_bastidor);
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                c.setAño(rs.getInt("año") + "");
                c.setKilometraje(rs.getDouble("kilometraje"));
                c.setDisponibleVentas(true);
                c.setDisponibleAlquiler(true);
                c.setPrecioBase(rs.getDouble("precio_base"));
                c.setColor(rs.getString("color"));
                c.setDisenoLlantas(rs.getString("diseño_llantas"));
                c.setDiametroLlantas(rs.getInt("diametro_llantas"));
                c.setDisenoInterior(rs.getString("diseño_interior"));
                c.setMaterialInterior(rs.getString("material_interior"));
                c.setMatricula(rs.getString("matricula"));

            }

        } catch (SQLException error) {
            System.out.println("Error al leer coches: " + error.getMessage());
        }

        return c;
    }

    // UPDATE
    public static void actualizarCoche(
            String numBastidor,
            String marca,
            String modelo,
            String año,
            String kilometraje,
            boolean disponibleVentas,
            boolean disponibleAlquiler,
            String precioBase,
            String color,
            String diseñoLlantas,
            String diametroLlantas,
            String diseñoInterior,
            String materialInterior,
            String matricula) throws SQLException {

        String sql = "UPDATE coche SET marca=?, modelo=?, año=?, kilometraje=?, disponible_ventas=?, " +
                "disponible_alquiler=?, precio_base=?, color=?, diseño_llantas=?, diametro_llantas=?, " +
                "diseño_interior=?, material_interior=?, matricula=? WHERE num_bastidor=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            // SET clause - en el orden del SQL
            ps.setString(1, marca); // marca
            ps.setString(2, modelo); // modelo
            ps.setInt(3, Integer.parseInt(año)); // año (INTEGER)
            ps.setDouble(4, Double.parseDouble(kilometraje)); // kilometraje (DOUBLE)
            ps.setBoolean(5, disponibleVentas); // disponible_ventas
            ps.setBoolean(6, disponibleAlquiler); // disponible_alquiler
            ps.setDouble(7, Double.parseDouble(precioBase)); // precio_base (DOUBLE)
            ps.setString(8, color); // color
            ps.setString(9, diseñoLlantas); // diseño_llantas
            ps.setInt(10, Integer.parseInt(diametroLlantas)); // diametro_llantas (INTEGER)
            ps.setString(11, diseñoInterior); // diseño_interior
            ps.setString(12, materialInterior); // material_interior
            ps.setString(13, matricula); // matricula

            // WHERE clause - al final
            ps.setString(14, numBastidor); // num_bastidor

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Coche actualizado correctamente. Filas actualizadas: " + filas);
            } else {
                System.out.println("No se encontró ningún coche con num_bastidor: " + numBastidor);
            }

        } catch (NumberFormatException e) {
            System.err.println("Error de formato numérico: " + e.getMessage());
            throw new SQLException("Valores numéricos inválidos", e);
        } catch (SQLException error) {
            System.err.println("Error al actualizar coche: " + error.getMessage());
            error.printStackTrace();
            throw error; // Re-lanzar para que el llamador lo maneje
        }
    }

    // DELETE
    public static void borrarCoche(String numBastidor) throws SQLException {
        String sql = "DELETE FROM coche WHERE num_bastidor = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, numBastidor);
            int filas = ps.executeUpdate();
            System.out.println("Filas borradas: " + filas);
        } catch (SQLException error) {
            System.out.println("Error al borrar coche: " + error.getMessage());
        }
    }

    // Obtener matrículas de coches asociados a un cliente
    public static List<String> obtenerMatriculasPorCliente(String dniCliente) {
        List<String> matriculas = new ArrayList<>();
        String sql = "SELECT DISTINCT id_vehiculo FROM transaccion WHERE cliente_dni = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dniCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                matriculas.add(rs.getString("id_vehiculo"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener matrículas del cliente: " + e.getMessage());
        }

        return matriculas;
    }

    // Obtener información completa de un coche por matrícula
    public static CocheCompleto buscarCochePorMatricula(String matricula) {
        CocheCompleto c = null;
        String sql = "SELECT * FROM coche WHERE matricula = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                c = new CocheCompleto(
                        rs.getString("num_bastidor"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("año"),
                        rs.getDouble("kilometraje"),
                        rs.getBoolean("disponible_ventas"),
                        rs.getBoolean("disponible_alquiler"),
                        rs.getDouble("precio_base"),
                        rs.getString("color"),
                        rs.getString("diseño_llantas"),
                        rs.getInt("diametro_llantas"),
                        rs.getString("diseño_interior"),
                        rs.getString("material_interior"),
                        rs.getString("matricula"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar coche por matrícula: " + e.getMessage());
        }

        return c;
    }

}