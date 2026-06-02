package com.javafx.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaccion {

    private final int    id;
    private final String tipo;
    private final String fecha;
    private final String cliente;
    private final String matricula;
    private final String monto;
    private final String agente;
    private final StringProperty estadoPago;

    public Transaccion(int id, String tipo, String fecha,
                       String cliente, String matricula,
                       String monto, String estadoPago) {
        this(id, tipo, fecha, cliente, matricula, monto, estadoPago, "");
    }

    public Transaccion(int id, String tipo, String fecha,
                       String cliente, String matricula,
                       String monto, String estadoPago, String agente) {
        this.id         = id;
        this.tipo       = tipo;
        this.fecha      = fecha;
        this.cliente    = cliente;
        this.matricula  = matricula;
        this.monto      = monto;
        this.estadoPago = new SimpleStringProperty(estadoPago);
        this.agente     = agente;
    }

    public int    getId()        { return id; }
    public String getTipo()      { return tipo; }
    public String getFecha()     { return fecha; }
    public String getCliente()   { return cliente; }
    public String getMatricula() { return matricula; }
    public String getMonto()     { return monto; }
    public String getAgente()    { return agente; }

    public String getEstadoPago()              { return estadoPago.get(); }
    public void   setEstadoPago(String estado) { estadoPago.set(estado); }
    public StringProperty estadoPagoProperty() { return estadoPago; }
}
