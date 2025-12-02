package com.javafx.Model;

public class CocheCompra {
    private String Marca;
    private String Modelo;
    private String Año;
    private String Precio;
    private String Color;
    private String DiseñoRuedas;
    private String Matricula;
    private String DiametroLlantas;
    private String Interior;
    private String Material;
    public CocheCompra(String marca, String modelo, String año, String precio, String color, String diseñoRuedas,
            String matricula, String diametroLlantas, String interior, String material) {
        Marca = marca;
        Modelo = modelo;
        Año = año;
        Precio = precio;
        Color = color;
        DiseñoRuedas = diseñoRuedas;
        Matricula = matricula;
        DiametroLlantas = diametroLlantas;
        Interior = interior;
        Material = material;
    }
    public String getMarca() {
        return Marca;
    }
    public void setMarca(String marca) {
        Marca = marca;
    }
    public String getModelo() {
        return Modelo;
    }
    public void setModelo(String modelo) {
        Modelo = modelo;
    }
    public String getAño() {
        return Año;
    }
    public void setAño(String año) {
        Año = año;
    }
    public String getPrecio() {
        return Precio;
    }
    public void setPrecio(String precio) {
        Precio = precio;
    }
    public String getColor() {
        return Color;
    }
    public void setColor(String color) {
        Color = color;
    }
    public String getDiseñoRuedas() {
        return DiseñoRuedas;
    }
    public void setDiseñoRuedas(String diseñoRuedas) {
        DiseñoRuedas = diseñoRuedas;
    }
    public String getMatricula() {
        return Matricula;
    }
    public void setMatricula(String matricula) {
        Matricula = matricula;
    }
    public String getDiametroLlantas() {
        return DiametroLlantas;
    }
    public void setDiametroLlantas(String diametroLlantas) {
        DiametroLlantas = diametroLlantas;
    }
    public String getInterior() {
        return Interior;
    }
    public void setInterior(String interior) {
        Interior = interior;
    }
    public String getMaterial() {
        return Material;
    }
    public void setMaterial(String material) {
        Material = material;
    }

    
    
    
    
    
    
    
}
