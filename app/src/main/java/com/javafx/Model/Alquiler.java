package com.javafx.Model;

public class Alquiler{
    private String fechaDevolucion;
    private String horaDevolucion;
    private String dni;
    private String matricula;
    public Alquiler(String fechaRecogida, String fechaEntrega, String dni, String matricula) {
        this.fechaDevolucion = fechaRecogida;
        this.horaDevolucion = fechaEntrega;
        this.dni = dni;
        this.matricula = matricula;
    }
    public String getFechaDevolucion() {
        return fechaDevolucion;
    }
    public String getHoraDevolucion() {
        return horaDevolucion;
    }
    public String getDni() {
        return dni;
    }
    public String getMatricula() {
        return matricula;
    }

    

}