package com.javafx.App;

import com.javafx.JDBC.Conexion;
import com.javafx.Model.Transaccion;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class controladorPagos {

    @FXML private Label                   lblTitulo;
    @FXML private TableView<Transaccion>  tblTransacciones;
    @FXML private TableColumn<Transaccion, Integer> colId;
    @FXML private TableColumn<Transaccion, String>  colFecha;
    @FXML private TableColumn<Transaccion, String>  colCliente;
    @FXML private TableColumn<Transaccion, String>  colMatricula;
    @FXML private TableColumn<Transaccion, String>  colMonto;
    @FXML private TableColumn<Transaccion, String>  colEstado;
    @FXML private ComboBox<String>        cmbNuevoEstado;

    private String tipoTransaccion;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));

        cmbNuevoEstado.getItems().addAll("Pendiente", "Pagado", "Cancelado");
        cmbNuevoEstado.getSelectionModel().select("Pagado");
    }

    public void setTipo(String tipo) {
        this.tipoTransaccion = tipo;
        String label = "VENTA".equals(tipo) ? "Ventas" : "Alquileres";
        lblTitulo.setText("Transacciones Pendientes de Pago — " + label);
        cargarDatos();
    }

    private void cargarDatos() {
        ObservableList<Transaccion> datos = Conexion.obtenerTransaccionesPendientes(tipoTransaccion);
        tblTransacciones.setItems(datos);
    }

    @FXML
    void actualizarEstado(ActionEvent event) {
        Transaccion seleccionada = tblTransacciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Selecciona una transacción",
                    "Elige una transacción de la tabla antes de cambiar su estado.");
            return;
        }

        String nuevoEstado = cmbNuevoEstado.getSelectionModel().getSelectedItem();
        if (nuevoEstado == null) return;

        if (nuevoEstado.equals(seleccionada.getEstadoPago())) {
            AlertaEstilizada.mostrarAdvertencia("Sin cambios",
                    "Estado igual al actual",
                    "El estado seleccionado es el mismo que ya tiene la transacción.");
            return;
        }

        boolean ok = Conexion.actualizarEstadoPago(seleccionada.getId(), nuevoEstado);
        if (ok) {
            seleccionada.setEstadoPago(nuevoEstado);
            tblTransacciones.refresh();
            // Si se marcó como Pagado o Cancelado, retirar de la lista de pendientes
            if (!"Pendiente".equals(nuevoEstado)) {
                tblTransacciones.getItems().remove(seleccionada);
            }
        } else {
            AlertaEstilizada.mostrarAdvertencia("Error",
                    "No se pudo actualizar",
                    "Ocurrió un error al actualizar el estado de pago. Inténtalo de nuevo.");
        }
    }

    @FXML
    void cerrar(ActionEvent event) {
        ((Stage) tblTransacciones.getScene().getWindow()).close();
    }
}
