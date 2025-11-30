package com.javafx.App;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.awt.Toolkit;

public class AlertaFallo{

   

    public static void mostrar(){
        Toolkit.getDefaultToolkit().beep();
        AlertType tipo = AlertType.ERROR;
        String mensaje = "Credenciales Incorrectas";
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText("Error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        
    }
    
}