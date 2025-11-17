package com.javafx.App;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class alertaDesarrollo {

   

    public static void mostrar(){
        AlertType tipo = AlertType.INFORMATION;
        String mensaje = "Funcion aun en desarrollo";
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText("Cuidado");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        
    }
    
}
