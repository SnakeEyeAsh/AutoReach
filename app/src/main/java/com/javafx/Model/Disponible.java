package com.javafx.Model;

public class Disponible {
    private String año;
    private String Color;
    private String Disponible;
    private String Precio;
    private String Matricula;
    public Disponible(String Año, String color, String disponible, String precio,String matricula) {
        this.año = Año;
        Color = color;
        Disponible = disponible;
        Precio = precio;
        Matricula=matricula;
    }
    public String getAño() {
        return año;
    }
    public String getColor() {
        return Color;
    }
    public String getDisponible() {
        return Disponible;
    }
    public String getPrecio() {
        return Precio;
    }
    public String getMatricula() {
        return Matricula;
    }

    
}
