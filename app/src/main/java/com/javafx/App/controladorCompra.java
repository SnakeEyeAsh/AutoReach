package com.javafx.App;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class controladorCompra implements Initializable{

    @FXML
    private Button btnRealizarCompra;

    @FXML
    private Button btnRevetirCompra;

    @FXML
    private TextField txtBanco;

    @FXML
    private TextField txtCInicial;

    @FXML
    private TextField txtCfinal;

    @FXML
    private TextField txtCuota;

    @FXML
    private TextField txtIban;

    @FXML
    private TextField txtTitular;

    @FXML
    void efectuarCompra(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Función en desarrollo",
            "Próximamente",
            "Esta funcionalidad estará disponible en una versión futura"
        );
    }

    @FXML
    void revertirCompra(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Función en desarrollo",
            "Próximamente",
            "Esta funcionalidad estará disponible en una versión futura"
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         Platform.runLater(()->{
            Stage s = (Stage)btnRealizarCompra.getScene().getWindow();
            s.getIcons().add(new Image("Assets/dar-dinero.png"));
        });
    }

}