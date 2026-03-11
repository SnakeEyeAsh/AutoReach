package com.javafx.App;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class controladorExitoCompra {

    @FXML private Label lblIdTransaccion;
    @FXML private Label lblVehiculo;
    @FXML private Label lblMatricula;
    @FXML private Label lblImporte;
    @FXML private Label lblCliente;
    @FXML private Label lblFecha;
    @FXML private Label lblEstadoPago;
    @FXML private Button btnCerrar;

    /**
     * Rellena todos los labels con los datos de la compra completada.
     * Llamar justo después de cargar el FXML, antes de mostrar la ventana.
     */
    public void setDatosExito(int transaccionId, String vehiculo, String matricula,
                               double importe, String cliente, String estadoPago) {
        lblIdTransaccion.setText(String.valueOf(transaccionId));
        lblVehiculo.setText(vehiculo);
        lblMatricula.setText(matricula);
        lblImporte.setText(String.format("%.2f €", importe));
        lblCliente.setText(cliente);
        lblFecha.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblEstadoPago.setText(estadoPago);
    }

    @FXML
    void cerrar(ActionEvent event) {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
