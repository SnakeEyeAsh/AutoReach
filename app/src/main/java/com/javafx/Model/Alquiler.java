package com.javafx.Model;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Alquiler{
    private Timestamp fechaDevolucion;
    private String horaDevolucion;
    private String dni;
    private String matricula;
    private Timestamp fechaRecogida;
    
    public Alquiler(Timestamp fecha, String fechaEntrega, String dni, String matricula,Timestamp fechaRecogida2) {
        this.fechaDevolucion = fecha;
        this.horaDevolucion = fechaEntrega;
        this.dni = dni;
        this.matricula = matricula;
        this.fechaRecogida=fechaRecogida2;
    }
    public Timestamp getFechaDevolucion() {
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
    public void setFechaDevolucion(Timestamp fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    public void setHoraDevolucion(String horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public Timestamp getFechaRecogida() {
        return fechaRecogida;
    }
    public void setFechaRecogida(Timestamp fechaRecogida) {
        this.fechaRecogida = fechaRecogida;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String getFechaRecogidaStr() {
        if (fechaRecogida == null) return "";
        return fechaRecogida.toLocalDateTime().toLocalDate().format(DATE_FMT);
    }

    public String getFechaDevolucionStr() {
        if (fechaDevolucion == null) return "";
        return fechaDevolucion.toLocalDateTime().toLocalDate().format(DATE_FMT);
    }

}