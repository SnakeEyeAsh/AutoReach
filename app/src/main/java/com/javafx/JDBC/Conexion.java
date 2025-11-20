package com.javafx.JDBC;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.javafx.Model.Alquiler;
import com.javafx.Model.Cita;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



//prueba de conexion a la base de datos
public class Conexion {

    private static Connection conexion;
    private static String url = "jdbc:postgresql://db.oynhtbeqraatfhsxyqee.supabase.co:5432/postgres";
    private static String user = "postgres";
    private static String password = "911GT3rs.rai";
    
    

    public Conexion(){
        
        
    }

    protected static Connection getConnection(){
        return conexion;
    }

    public static void realizarConexion() {
        try {
            conexion = DriverManager.getConnection(url,user,password);
            System.out.println("conexion realizada");
            estadoConexion = true;
        
        } catch (SQLException e) {
            System.out.println(e.getMessage()+"el problemas es la conexion");
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

                Cliente c = new Cliente(nombre, apellidos, dni, Integer.parseInt(telefono), mail);

                lista.add(c);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("NO hace el try");
        }
        return lista;
    }

    public static ObservableList<Cita> mostrarCitas() {

        ObservableList<Cita> lista = FXCollections.observableArrayList();

        try {
            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery("SELECT * FROM cita;");
            while (resultado.next()) {
                String num_bastidor = resultado.getString("num_bastidor_coche");
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
                String dni = resultado.getString("cliente_id");
                String bastidor = resultado.getString("vehiculo_id");

                LocalDate dia = fecha.toLocalDateTime().toLocalDate();

                String[] partes = hora.split(" ");

                Alquiler a = new Alquiler(dia.toString(), partes[1], dni, bastidor);

                lista.add(a);
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta de citas " + error.getMessage());
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


                Coche c = new Coche(marca, modelo, año+"",  kms+" Km", bastidor, precio+"", diseño, diseñoLlantas, color);

                lista.add(c);
            }

        } catch (SQLException error) {
            System.out.println("Error en la consulta de citas " + error.getMessage());
        }
        return lista;

    }
   

    




}