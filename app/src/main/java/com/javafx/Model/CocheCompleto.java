package com.javafx.Model;

import java.math.BigDecimal;

public class CocheCompleto {
    private String numBastidor;
    private String marca;
    private String modelo;
    private Integer año;
    private Double kilometraje;
    private Boolean disponibleVentas;
    private Boolean disponibleAlquiler;
    private Double precioBase;
    private String color;
    private String disenoLlantas;
    private Integer diametroLlantas;
    private String disenoInterior;
    private String materialInterior;
    private String matricula;

    // Getters y setters
    public String getNumBastidor() {
        return numBastidor;
    }

    public void setNumBastidor(String numBastidor) {
        this.numBastidor = numBastidor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = Integer.parseInt(año);
    }

    public Double getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Double kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Boolean getDisponibleVentas() {
        return disponibleVentas;
    }

    public void setDisponibleVentas(boolean disponibleVentas) {
        this.disponibleVentas = disponibleVentas;
    }

    public Boolean getDisponibleAlquiler() {
        return disponibleAlquiler;
    }

    public void setDisponibleAlquiler(Boolean disponibleAlquiler) {
        this.disponibleAlquiler = disponibleAlquiler;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDisenoLlantas() {
        return disenoLlantas;
    }

    public void setDisenoLlantas(String disenoLlantas) {
        this.disenoLlantas = disenoLlantas;
    }

    public Integer getDiametroLlantas() {
        return diametroLlantas;
    }

    public void setDiametroLlantas(int diametroLlantas) {
        this.diametroLlantas = diametroLlantas;
    }

    public String getDisenoInterior() {
        return disenoInterior;
    }

    public void setDisenoInterior(String disenoInterior) {
        this.disenoInterior = disenoInterior;
    }

    public String getMaterialInterior() {
        return materialInterior;
    }

    public void setMaterialInterior(String materialInterior) {
        this.materialInterior = materialInterior;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + numBastidor + ")";
    }

    public CocheCompleto(String numBastidor, String marca, String modelo, Integer año, Double kilometraje,
            Boolean disponibleVentas, Boolean disponibleAlquiler, Double precioBase, String color, String disenoLlantas,
            Integer diametroLlantas, String disenoInterior, String materialInterior, String matricula) {
        this.numBastidor = numBastidor;
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.kilometraje = kilometraje;
        this.disponibleVentas = disponibleVentas;
        this.disponibleAlquiler = disponibleAlquiler;
        this.precioBase = precioBase;
        this.color = color;
        this.disenoLlantas = disenoLlantas;
        this.diametroLlantas = diametroLlantas;
        this.disenoInterior = disenoInterior;
        this.materialInterior = materialInterior;
        this.matricula = matricula;
    }

    

    
}
