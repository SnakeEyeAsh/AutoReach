package com.javafx.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Documento {

    private final int    id;
    private final String tipo;
    private final String cliente;
    private final String transaccionInfo;
    private final String fechaCreacion;
    private final StringProperty estado;
    private final String fechaFirma;
    private final String firmadoPor;

    public Documento(int id, String tipo, String cliente,
                     String transaccionInfo, String fechaCreacion,
                     String estado, String fechaFirma, String firmadoPor) {
        this.id              = id;
        this.tipo            = tipo;
        this.cliente         = cliente;
        this.transaccionInfo = transaccionInfo;
        this.fechaCreacion   = fechaCreacion;
        this.estado          = new SimpleStringProperty(estado);
        this.fechaFirma      = fechaFirma;
        this.firmadoPor      = firmadoPor;
    }

    public int    getId()              { return id; }
    public String getTipo()            { return tipo; }
    public String getCliente()         { return cliente; }
    public String getTransaccionInfo() { return transaccionInfo; }
    public String getFechaCreacion()   { return fechaCreacion; }
    public String getFechaFirma()      { return fechaFirma; }
    public String getFirmadoPor()      { return firmadoPor; }

    public String         getEstado()              { return estado.get(); }
    public void           setEstado(String e)      { estado.set(e); }
    public StringProperty estadoProperty()         { return estado; }
}
