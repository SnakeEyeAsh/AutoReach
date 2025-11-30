package com.javafx.Model;

public class Coche{

    private String Marca;
    private String Modelo;
    private String Matricula;
    private String Año;
    private String kms;
    private String Bastidor;
    private String Precio;
    private String Diseño;
    private String DiametroLlantas;
    private String Color;
    public Coche(String marca, String modelo,String matricula, String año, String kms, String bastidor, String precio, 
            String diseño, String diseñoLlantas, String color) {
        Marca = marca;
        Modelo = modelo;
        Matricula = matricula;
        Año = año;
        this.kms = kms;
        Bastidor = bastidor;
        Precio = precio;
        Diseño = diseño;
        DiametroLlantas = diseñoLlantas;
        Color = color;
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
    public String getKms() {
        return kms;
    }
    public void setKms(String kms) {
        this.kms = kms;
    }
    public String getBastidor() {
        return Bastidor;
    }
    public void setBastidor(String bastidor) {
        Bastidor = bastidor;
    }
    public String getPrecio() {
        return Precio;
    }
    public void setPrecio(String precio) {
        Precio = precio;
    }
    public String getDiseño() {
        return Diseño;
    }
    public void setDiseño(String diseño) {
        Diseño = diseño;
    }
    public String getDiametroLlantas() {
        return DiametroLlantas;
    }
    public void setDiametroLlantas(String diseñoLlantas) {
        DiametroLlantas = diseñoLlantas;
    }
    public String getColor() {
        return Color;
    }
    public void setColor(String color) {
        Color = color;
    }

    

}