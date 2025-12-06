package com.javafx.App;

import java.awt.Toolkit;

public class AlertaFallo{

    public static void mostrar(){
        Toolkit.getDefaultToolkit().beep();
        AlertaEstilizada.mostrarError(
            "Error de autenticación",
            "Error",
            "Credenciales Incorrectas"
        );
    }

}