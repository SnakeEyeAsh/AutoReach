package com.javafx.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

public class controladorEmergente {

    @FXML
    private SplitMenuButton MenuTiposPago;

    @FXML
    private Button brnCancelarCompra;

    @FXML
    private Button btnAceptarCompra;

    @FXML
    private TextField txtBanco;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtFecha;

    @FXML
    private TextField txtIban;

    @FXML
    void RealizarCompra(ActionEvent event) {
        AlertaEstilizada.crearAdvertencia("Cuidado", "Esta seccion aun esta en desarrollo","Vuelve atras este es un limite de la aplicacion esta parte aun no esta programada" );
    }

    @FXML
    void btnCancelarCompra(ActionEvent event) {
        AlertaEstilizada.crearAdvertencia("Cuidado", "Esta seccion aun esta en desarrollo","Vuelve atras este es un limite de la aplicacion esta parte aun no esta programada" );
    }

}
