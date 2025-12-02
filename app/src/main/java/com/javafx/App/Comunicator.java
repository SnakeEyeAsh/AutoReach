package com.javafx.App;

public class Comunicator {
    private String Banco;
    private String TipoPago;
    private String Iban;
    private String CodigSeguridad;
    private String fechaCaducidad;
    
    // Instancia única (static)
    private static Comunicator instancia;
    
    // Constructor PRIVADO para evitar instanciación externa
    private Comunicator(String banco, String tipoPago, String iban, String codigSeguridad, String fechaCaducidad) {
        this.Banco = banco;
        this.TipoPago = tipoPago;
        this.Iban = iban;
        this.CodigSeguridad = codigSeguridad;
        this.fechaCaducidad = fechaCaducidad;
    }
    
    // Método para obtener la instancia (DEBE RETORNAR Comunicator)
    public static Comunicator getInstancia() {
        if (instancia == null) {
            instancia = new Comunicator(null, null, null, null, null); // Faltaba el punto y coma ;
        }
        return instancia; // FALTABA ESTO
    }
    
    // Método para actualizar los datos
    public void setDatos(String banco, String tipoPago, String iban, String codigSeguridad, String fechaCaducidad) {
        this.Banco = banco;
        this.TipoPago = tipoPago;
        this.Iban = iban;
        this.CodigSeguridad = codigSeguridad;
        this.fechaCaducidad = fechaCaducidad;
    }
    
    // Getters
    public String getBanco() {
        return Banco;
    }
    
    public String getTipoPago() {
        return TipoPago;
    }
    
    public String getIban() {
        return Iban;
    }
    
    public String getCodigSeguridad() {
        return CodigSeguridad;
    }
    
    public String getFechaCaducidad() {
        return fechaCaducidad;
    }
}

