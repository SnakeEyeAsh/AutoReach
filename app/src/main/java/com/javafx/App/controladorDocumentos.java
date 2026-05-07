package com.javafx.App;

import com.javafx.JDBC.Conexion;
import com.javafx.Model.Documento;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class controladorDocumentos {

    @FXML private Label                   lblTitulo;
    @FXML private ComboBox<String>        cmbFiltroEstado;
    @FXML private TableView<Documento>    tblDocumentos;
    @FXML private TableColumn<Documento, Integer> colId;
    @FXML private TableColumn<Documento, String>  colTipo;
    @FXML private TableColumn<Documento, String>  colCliente;
    @FXML private TableColumn<Documento, String>  colTransaccion;
    @FXML private TableColumn<Documento, String>  colFecha;
    @FXML private TableColumn<Documento, String>  colEstado;
    @FXML private TextField               txtFirmadoPor;

    private String tipoDocumento;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colTransaccion.setCellValueFactory(new PropertyValueFactory<>("transaccionInfo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbFiltroEstado.getItems().addAll("Todos", "PENDIENTE_FIRMA", "FIRMADO", "CANCELADO");
        cmbFiltroEstado.getSelectionModel().selectFirst();
    }

    public void setTipo(String tipo) {
        this.tipoDocumento = tipo;
        String label = "CONTRATO_VENTA".equals(tipo) ? "Ventas" : "Alquileres";
        lblTitulo.setText("Documentos — " + label);
        cargarDatos();
    }

    private void cargarDatos() {
        String filtroEstado = cmbFiltroEstado.getSelectionModel().getSelectedItem();
        String estado = "Todos".equals(filtroEstado) ? null : filtroEstado;
        ObservableList<Documento> datos = Conexion.obtenerDocumentos(tipoDocumento, estado);
        tblDocumentos.setItems(datos);
    }

    @FXML
    void aplicarFiltro(ActionEvent event) {
        cargarDatos();
    }

    @FXML
    void firmarDocumento(ActionEvent event) {
        Documento sel = tblDocumentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Selecciona un documento",
                    "Elige un documento de la tabla antes de firmarlo.");
            return;
        }
        if ("FIRMADO".equals(sel.getEstado())) {
            AlertaEstilizada.mostrarAdvertencia("Ya firmado",
                    "El documento ya está firmado",
                    "Este documento ya tiene estado FIRMADO.");
            return;
        }
        if ("CANCELADO".equals(sel.getEstado())) {
            AlertaEstilizada.mostrarAdvertencia("Documento cancelado",
                    "No se puede firmar",
                    "No es posible firmar un documento cancelado.");
            return;
        }

        String firmante = txtFirmadoPor.getText().trim();
        if (firmante.isEmpty()) {
            AlertaEstilizada.mostrarAdvertencia("Campo requerido",
                    "Indica el firmante",
                    "Escribe el nombre del firmante antes de continuar.");
            return;
        }

        boolean ok = Conexion.firmarDocumento(sel.getId(), firmante);
        if (ok) {
            sel.setEstado("FIRMADO");
            tblDocumentos.refresh();
        } else {
            AlertaEstilizada.mostrarAdvertencia("Error",
                    "No se pudo firmar",
                    "Ocurrió un error al firmar el documento. Inténtalo de nuevo.");
        }
    }

    @FXML
    void cancelarDocumento(ActionEvent event) {
        Documento sel = tblDocumentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Selecciona un documento",
                    "Elige un documento de la tabla antes de cancelarlo.");
            return;
        }
        if ("CANCELADO".equals(sel.getEstado())) {
            AlertaEstilizada.mostrarAdvertencia("Ya cancelado",
                    "El documento ya está cancelado",
                    "Este documento ya tiene estado CANCELADO.");
            return;
        }

        boolean ok = Conexion.cancelarDocumento(sel.getId());
        if (ok) {
            sel.setEstado("CANCELADO");
            tblDocumentos.refresh();
        } else {
            AlertaEstilizada.mostrarAdvertencia("Error",
                    "No se pudo cancelar",
                    "Ocurrió un error al cancelar el documento. Inténtalo de nuevo.");
        }
    }

    @FXML
    void cerrar(ActionEvent event) {
        ((Stage) tblDocumentos.getScene().getWindow()).close();
    }
}
