package com.javafx.App;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.javafx.JDBC.Conexion;
import com.javafx.Model.CocheCompra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class controladorEmergente {

    // ── Campos del FXML ───────────────────────────────────────────
    // Sección vehículo (readonly)
    @FXML private Label lblVehiculo;   // marca + modelo
    @FXML private Label lblMatricula;  // matricula — clave para buscar vin_coche
    @FXML private Label lblMonto;      // monto_total

    // Sección transacción (readonly + editable)
    @FXML private Label              lblCliente;     // nombre del cliente
    @FXML private Label              lblFecha;       // fecha_transaccion (informativa)
    @FXML private ComboBox<String>   cmbEstadoPago;  // estado_pago

    // Botones
    @FXML private Button btnAceptarCompra;
    @FXML private Button brnCancelarCompra;

    // ── Datos pasados por controladorApp ─────────────────────────
    private int    clienteId     = -1;
    private String clienteNombre = "";

    public void setClienteId(int id) {
        this.clienteId = id;
    }

    public void setClienteNombre(String nombre) {
        this.clienteNombre = nombre;
        if (lblCliente != null) lblCliente.setText(nombre);
    }

    // ── Inicialización ────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Poblar ComboBox desde código (evita problemas de tipo en FXML)
        cmbEstadoPago.getItems().addAll("Pendiente", "Pagado");
        cmbEstadoPago.getSelectionModel().selectFirst();

        // Fecha actual (informativa; la BD usa DEFAULT CURRENT_TIMESTAMP)
        String ahora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        lblFecha.setText(ahora);

        // Rellenar datos del vehículo desde el campo estático
        CocheCompra coche = controladorApp.cocheParaComprar;
        if (coche != null) {
            lblVehiculo.setText(coche.getMarca() + " " + coche.getModelo()
                    + " (" + coche.getAño() + ")");
            lblMatricula.setText(coche.getMatricula());
            lblMonto.setText(coche.getPrecio() + " €");
        }
    }

    // ── Confirmar compra ──────────────────────────────────────────
    @FXML
    void RealizarCompra(ActionEvent event) {

        CocheCompra coche = controladorApp.cocheParaComprar;
        if (coche == null || clienteId <= 0) {
            AlertaEstilizada.crearAdvertencia("Error", "Datos incompletos",
                    "No se pudo obtener la información del vehículo o del cliente.");
            return;
        }

        String estadoPago = cmbEstadoPago.getSelectionModel().getSelectedItem();
        if (estadoPago == null) {
            AlertaEstilizada.crearAdvertencia("Campo requerido", "Estado de pago",
                    "Selecciona el estado de pago.");
            return;
        }

        // Parsear monto del vehículo
        double monto;
        try {
            monto = Double.parseDouble(coche.getPrecio().replace(",", "."));
        } catch (NumberFormatException e) {
            AlertaEstilizada.crearAdvertencia("Error", "Precio inválido",
                    "El precio del vehículo no es válido: " + coche.getPrecio());
            return;
        }

        // ── Registrar en transacciones ───────────────────────────
        int transaccionId = Conexion.registrarVenta(
                coche.getMatricula(), clienteId, monto, estadoPago);

        if (transaccionId <= 0) {
            AlertaEstilizada.crearAdvertencia("Error de conexión", "No se pudo registrar",
                    "Ocurrió un error al guardar la transacción. Inténtalo de nuevo.");
            return;
        }

        // ── Marcar coche como vendido en coches ──────────────────
        Conexion.marcarComoVendido(coche.getMatricula());

        // ── Éxito: cerrar confirmación y abrir ventana de éxito ───
        cerrarDialogo();
        mostrarVentanaExito(transaccionId, coche, monto, estadoPago);
    }

    @FXML
    void btnCancelarCompra(ActionEvent event) {
        cerrarDialogo();
    }

    private void cerrarDialogo() {
        ((Stage) brnCancelarCompra.getScene().getWindow()).close();
    }

    private void mostrarVentanaExito(int transaccionId, CocheCompra coche,
                                     double monto, String estadoPago) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ventanaExitoCompra.fxml"));
            Parent root = loader.load();

            controladorExitoCompra ctrl = loader.getController();
            ctrl.setDatosExito(
                    transaccionId,
                    coche.getMarca() + " " + coche.getModelo() + " (" + coche.getAño() + ")",
                    coche.getMatricula(),
                    monto,
                    clienteNombre,
                    estadoPago);

            Stage stage = new Stage();
            stage.setTitle("Compra completada");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al abrir ventana de éxito: " + e.getMessage());
        }
    }
}
