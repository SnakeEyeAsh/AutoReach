package com.javafx.App;

import javafx.scene.control.Button;

public class cambiarColores {

    private static final String ACTIVO =
        "-fx-background-color: rgba(0,212,255,0.13); -fx-text-fill: white;" +
        " -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-padding: 10 0;";

    private static final String INACTIVO =
        "-fx-background-color: transparent; -fx-text-fill: #b0bcd4;" +
        " -fx-font-size: 11; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-padding: 10 0;";

    public static void cambiar(Button presente, Button dos, Button tres, Button cuatro) {
        presente.setStyle(ACTIVO);
        dos.setStyle(INACTIVO);
        tres.setStyle(INACTIVO);
        cuatro.setStyle(INACTIVO);
    }

}
