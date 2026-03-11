package com.javafx.Model;



public class Cliente {

    private int id;
    private String nombre;
    private String apellidos;
    private String dni;
    private long telefono;  // Cambiado de double a long para evitar perdida de precision
    private String email;
    private String direccion;

    public Cliente(String nombre, String apellidos, String dni, long telefono, String email, String direccion) {

        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getDireccion() {
        return direccion;
    }



    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    

    

}
